import java.io.File;
import java.io.FileNotFoundException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Scanner;

import com.botticelli.bot.Bot;
import com.botticelli.messagereceiver.MessageReceiver;

public class Main {

    public static String filePath;

    public static void main(String[] args) throws FileNotFoundException, IllegalArgumentException, UnknownHostException, SocketException
    {
        filePath = new File("").getAbsolutePath() + System.getProperty("file.separator");
        File tokenFile = new File(filePath + "token.txt");
        String token = "";
        try (Scanner s = new Scanner(tokenFile))
        {
            while (s.hasNext())
            {
                token = s.nextLine();
            }
        }
        //Bot bot = new botbase.BotBase(token);
        //Bot bot = new randommedia.RandomMediaBot(token);
        //Bot bot = new botspesa.BotSpesa(token);
        //Bot bot = new questionario.QuestionarioBot(token);
        //Bot bot = new PrimoBot(token);
        //Bot bot = new cartamorra.CartaMorraBot(token);
        //Bot bot = new figucciamorra.BotMorraCinese(token);
        //Bot bot = new TicTacToeBot(token);


       // MessageReceiver mr = new MessageReceiver(bot, 500, 1);



        //mr.ignoreEditedMessages();
        //mr.start();

    }
}
