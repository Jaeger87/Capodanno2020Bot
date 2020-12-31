public class Penance {

    private String text;
    private int duration;


    public Penance(String text, int duration) {
        this.text = text;
        this.duration = duration;
    }

    public String getText() {
        return text;
    }

    public int getDuration() {
        return duration;
    }

    public String getTextWithScadenza()
    {
        return text + "... È appena scaduta.";
    }
}
