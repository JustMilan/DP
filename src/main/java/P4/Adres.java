package P4;

public class Adres {
    private int id;
    private String postcode;
    private String huisnummer;
    private String straat;
    private String woonplaasts;
    private int reizigerId;

    public Adres(int id, String postcode, String huisnummer, String straat, String woonplaasts, int reizigerId) {
        this.id = id;
        this.postcode = postcode;
        this.huisnummer = huisnummer;
        this.straat = straat;
        this.woonplaasts = woonplaasts;
        this.reizigerId = reizigerId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(String huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getStraat() {
        return straat;
    }

    public void setStraat(String straat) {
        this.straat = straat;
    }

    public String getWoonplaasts() {
        return woonplaasts;
    }

    public void setWoonplaasts(String woonplaasts) {
        this.woonplaasts = woonplaasts;
    }

    public int getReizigerId() {
        return reizigerId;
    }

    public void setReizigerId(int reizigerId) {
        this.reizigerId = reizigerId;
    }

    @Override
    public String toString() {
        return "Adres:" +
                "id = " + id +
                ", postcode = '" + postcode + '\'' +
                ", huisnummer = '" + huisnummer + '\'' +
                ", straat = '" + straat + '\'' +
                ", woonplaasts = '" + woonplaasts + '\'' +
                ", reizigerId = '" + reizigerId;
    }
}
