import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.types.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Random;

public class CapodannoBot extends Bot {


    private List<Penance> penances;
    private HashMap<User, Boolean> chatMembers;
    private Random random;
    private ReplyKeyboardMarkupWithButtons tastiera;

    private Stack<User> turnoCorrente;
    private Stack<Penance> penitenzeToDoIt;

    private long chat_id = -318728780;


    private HashMap<Penance, Long> currentTimePenalties;

    public CapodannoBot(String token) {
        super(token);
        random = new Random();
        chatMembers = new HashMap<>();
        penances = new ArrayList<>();
        currentTimePenalties = new HashMap<>();

        String penancesFilePath = new File("").getAbsolutePath() + System.getProperty("file.separator");
        File tokenFile = new File(penancesFilePath + "penances.txt");
        try (Scanner s = new Scanner(tokenFile))
        {
            while (s.hasNext())
            {
                String[] parsed = s.nextLine().split("\t");
                penances.add(new Penance(parsed[0], Integer.valueOf(parsed[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<List<KeyboardButton>> keyboard = new ArrayList<>();
        List<KeyboardButton> line = new ArrayList<>();
        line.add(new KeyboardButton("\uD83C\uDFB2"));
        line.add(new KeyboardButton("\uD83C\uDFAF"));
        line.add(new KeyboardButton("⚽️"));
        keyboard.add(line);
        tastiera = new ReplyKeyboardMarkupWithButtons(keyboard);
        tastiera.setResizeKeyboard(true);

        penitenzeToDoIt = new Stack<>();

        resetPenitenzeToDoIt();
    }


    private void resetPenitenzeToDoIt()
    {
        penitenzeToDoIt.addAll(penances);

        Collections.shuffle(penitenzeToDoIt);
    }

    @Override
    public void textMessage(Message message) {
        User u = message.getFrom();
        if(!chatMembers.containsKey(u))
            chatMembers.put(message.getFrom(), true);

        String text = message.getText();

        if(text.startsWith("/")) //Arrivato un comando
        {
            Comando c = Comando.fromString(text);
            MessageToSend mts = null;
            switch (c) {
                case NEXT:
                    if(penitenzeToDoIt.isEmpty())
                        resetPenitenzeToDoIt();
                    Penance p = penitenzeToDoIt.pop();
                    mts = new MessageToSend(message.getChat().getId(), p.getText());
                    mts.setReplyMarkup(tastiera);
                    sendMessage(mts);

                    if(p.getDuration() > -1)
                        currentTimePenalties.put(p, System.currentTimeMillis() + 1000 * 60 * p.getDuration());
                    break;

                case RITIRO:
                    chatMembers.put(message.getFrom(), false);
                    mts = new MessageToSend(message.getChat().getId(), "Ok, il gioco per te finisce caro/a/i " + u.getUserName() != null ? u.getUserName() : u.getFirstName() + ". Non verrai più preso/a/i in considerazione per i prossimi turni.");
                    sendMessage(mts);
                    break;
                case ERRORE:
                    break;
                case NEWTURN:

                    break;
                default:
                    break;
            }

            return;
        }

    }





    @Override
    public void audioMessage(Message message) {

    }

    @Override
    public void videoMessage(Message message) {

    }

    @Override
    public void voiceMessage(Message message) {

    }

    @Override
    public void stickerMessage(Message message) {

    }

    @Override
    public void documentMessage(Message message) {

    }

    @Override
    public void photoMessage(Message message) {

    }

    @Override
    public void diceMessage(Message message) {

    }

    @Override
    public void contactMessage(Message message) {

    }

    @Override
    public void locationMessage(Message message) {

    }

    @Override
    public void venueMessage(Message message) {

    }

    @Override
    public void newChatMemberMessage(Message message) {

    }

    @Override
    public void newChatMembersMessage(Message message) {
        for(User u : message.getNewChatMembers())
        {
            chatMembers.put(u, true);
            MessageToSend mts = new MessageToSend(message.getChat().getId(), "Benvenuto/a/i " + u.getUserName() != null ? u.getUserName() : u.getFirstName() + " ");
            mts.setReplyMarkup(tastiera);
            sendMessage(mts);
        }
    }

    @Override
    public void leftChatMemberMessage(Message message) {
        chatMembers.remove(message.getLeftChatMember());
        sendMessage(new MessageToSend(message.getChat().getId(), "NOOOOOOOO"));
    }

    @Override
    public void newChatTitleMessage(Message message) {

    }

    @Override
    public void newChatPhotoMessage(Message message) {

    }

    @Override
    public void groupChatPhotoDeleteMessage(Message message) {

    }

    @Override
    public void groupChatCreatedMessage(Message message) {

    }

    @Override
    public void inLineQuery(InlineQuery inlineQuery) {

    }

    @Override
    public void chose_inline_result(ChosenInlineResult chosenInlineResult) {

    }

    @Override
    public void callback_query(CallbackQuery callbackQuery) {

    }

    @Override
    public void gameMessage(Message message) {

    }

    @Override
    public void videoNoteMessage(Message message) {

    }

    @Override
    public void pinnedMessage(Message message) {

    }

    @Override
    public void preCheckOutQueryMessage(PreCheckoutQuery preCheckoutQuery) {

    }

    @Override
    public void shippingQueryMessage(ShippingQuery shippingQuery) {

    }

    @Override
    public void invoiceMessage(Message message) {

    }

    @Override
    public void successfulPaymentMessage(Message message) {

    }

    @Override
    public void routine() {
        if (currentTimePenalties.isEmpty())
            return;

        List<Penance> keyToremove = new ArrayList<>();

        for(Penance p : currentTimePenalties.keySet())
        {
            if(currentTimePenalties.get(p) < System.currentTimeMillis()) {
                keyToremove.add(p);
                sendMessage(new MessageToSend(chat_id, p.getTextWithScadenza()));
            }
        }

        for(Penance p : keyToremove)
            currentTimePenalties.remove(p);
    }
}
