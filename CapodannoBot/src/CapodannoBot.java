import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.ChatRequests;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.types.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CapodannoBot extends Bot {


    private Set<User> admins;
    private List<Penance> penances;
    private HashMap<User, Boolean> chatMembers;
    private Random random;
    private ReplyKeyboardMarkupWithButtons tastiera;

    private Stack<User> turnoCorrente;
    private Stack<Penance> penitenzeToDoIt;

    private long chat_id = -1001477444974L;

    private List<String> insulti;

    private HashMap<Penance, Long> currentTimePenalties;

    public CapodannoBot(String token) {
        super(token);
        random = new Random();
        chatMembers = new HashMap<>();
        penances = new ArrayList<>();
        insulti = new ArrayList<>();
        currentTimePenalties = new HashMap<>();

        String penancesFilePath = new File("").getAbsolutePath() + System.getProperty("file.separator");
        File penancesFile = new File(penancesFilePath + "penances.txt");
        try (Scanner s = new Scanner(penancesFile))
        {
            while (s.hasNext())
            {
                String[] parsed = s.nextLine().split("\t");
                penances.add(new Penance(parsed[0], Integer.valueOf(parsed[1])));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        File insultiFile = new File(penancesFilePath + "insulti.txt");
        try (Scanner s = new Scanner(insultiFile))
        {
            while (s.hasNext())
            {
                insulti.add(s.nextLine());
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
        turnoCorrente = new Stack<>();
        resetPenitenzeToDoIt();

        admins = new HashSet<>();
        updateAdmins();
    }


    private void resetPenitenzeToDoIt()
    {
        penitenzeToDoIt.addAll(penances);
        Collections.shuffle(penitenzeToDoIt);
    }

    private void startTurn()
    {
        List<User> utentiAttivi = chatMembers.keySet().stream().filter(k->chatMembers.get(k)).collect(Collectors.toList());
        turnoCorrente.addAll(utentiAttivi);
        Collections.shuffle(turnoCorrente);
    }


    private String getNameUsername(User u)
    {
        return u.getUserName() != null ? "@" + u.getUserName() : u.getFirstName();
    }

    @Override
    public void textMessage(Message message) {
        if(message.getChat().getId() != chat_id)
            return;
        User u = message.getFrom();

        if(!chatMembers.containsKey(u))
            chatMembers.put(message.getFrom(), true);

        String text = message.getText();

        if(text.startsWith("/")) //Arrivato un comando
        {
            if (text.endsWith("@andra_tutto_bene_bot"))
                text = text.substring(0, text.indexOf('@'));
            Comando c = Comando.fromString(text);
            MessageToSend mts = null;
            switch (c) {
                case NEXT:
                    if(!admins.contains(u))
                    {
                        sendMessage(new MessageToSend(message.getChat().getId(), "Solo gli admin possono scorrere le penitenze"));
                        return;
                    }
                    if(turnoCorrente.isEmpty())
                    {
                        sendMessage(new MessageToSend(message.getChat().getId(), "Il turno non è stato inizializzato, usare il comando /newturn"));
                        return;
                    }
                    if(penitenzeToDoIt.isEmpty())
                        resetPenitenzeToDoIt();
                    Penance p = penitenzeToDoIt.pop();
                    User userTopunish = turnoCorrente.pop();
                    mts = new MessageToSend(message.getChat().getId(), getNameUsername(userTopunish) + "\n\n" + p.getText());
                    mts.setReplyMarkup(tastiera);
                    sendMessage(mts);

                    if(turnoCorrente.isEmpty())
                    {
                        sendMessage(new MessageToSend(message.getChat().getId(), "E con questa il turno si conclude! ricordate di fare /newturn se volete continuare a perdere la dignità.."));
                    }

                    if(p.getDuration() > -1)
                        currentTimePenalties.put(p, System.currentTimeMillis() + 1000 * 60 * p.getDuration());
                    break;

                case RITIRO:
                    chatMembers.put(message.getFrom(), false);
                    mts = new MessageToSend(message.getChat().getId(), "Ok, il gioco per te finisce caro/a/i " + u.getUserName() != null ? u.getUserName() : u.getFirstName() + ". Non verrai più preso/a/i in considerazione per i prossimi turni.");
                    sendMessage(mts);
                    break;
                case INSULTA:
                    int size = chatMembers.size();
                    int item = random.nextInt(size); // In real life, the Random object should be rather more shared than this
                    int i = 0;
                    int insultoIndex = random.nextInt(insulti.size());
                    for(User memberToInsult : chatMembers.keySet())
                    {
                        if (i == item) {
                            sendMessage(new MessageToSend(chat_id, getNameUsername(memberToInsult) + " " + insulti.get(insultoIndex)));
                            break;
                        }
                        i++;
                    }
                    break;
                case ERRORE:

                    break;
                case NEWTURN:
                    if(!admins.contains(u))
                    {
                        sendMessage(new MessageToSend(message.getChat().getId(), "Solo gli admin possono iniziare nuovi turni."));
                        return;
                    }
                    startTurn();
                    sendMessage(new MessageToSend(message.getChat().getId(), "Che un nuovo turno abbia inizio \uD83D\uDE08\uD83D\uDE08\uD83D\uDE08, gli admin possono usare da ora il comando /next per far girare le penitenze."));
                    break;
                case UPDATEADMINS:
                    updateAdmins();
                    sendMessage(new MessageToSend(message.getChat().getId(), "Lista admins aggiornata."));
                default:
                    break;
            }

            return;
        }

    }



    private void updateAdmins()
    {
        List<ChatMember> adminsList = getChatAdministrators(new ChatRequests(chat_id));
        if(adminsList == null)
            return;
        admins.addAll(adminsList.stream().map(m -> m.getUser()).collect(Collectors.toList()));
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
        if(message.getChat().getId() != chat_id)
            return;
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
        if(message.getChat().getId() != chat_id)
            return;
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
