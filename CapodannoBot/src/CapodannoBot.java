import com.botticelli.bot.Bot;
import com.botticelli.bot.request.methods.DiceToSend;
import com.botticelli.bot.request.methods.MessageToSend;
import com.botticelli.bot.request.methods.types.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class CapodannoBot extends Bot {


    private HashMap<User, Boolean> chatMembers;

    public CapodannoBot(String token) {
        super(token);
        chatMembers = new HashMap<>();
    }

    @Override
    public void textMessage(Message message) {
        if(!chatMembers.containsKey(message.getFrom()))
            chatMembers.put(message.getFrom(), true);

        String text = message.getText();
        if(text.startsWith("/")) //Arrivato un comando
        {
            Comando c = Comando.fromString(text);
            switch (c) {
                case NEXT:

                    break;

                case RITIRO:
                    chatMembers.put(message.getFrom(), false);
                    break;
                case ERRORE:
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
            sendMessage(new MessageToSend(message.getChat().getId(), "Benvenuto/a/i " + u.getUserName() != null ? u.getUserName() : u.getFirstName() + " "));
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

    }
}
