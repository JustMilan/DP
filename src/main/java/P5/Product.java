package P5;

import java.util.ArrayList;
import java.util.List;

public class Product {
    private int productNummer;
    private String naam;
    private String beschrijving;
    private float prijs;
    private List<OVChipkaart> ovChipkaart;

    public Product(int productNummer, String naam, String beschrijving, float prijs) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
        this.ovChipkaart = new ArrayList<OVChipkaart>();
    }

    public Product(int productNummer, String naam, String beschrijving, float prijs, List<OVChipkaart> ovChipkaart) {
        this.productNummer = productNummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
        this.ovChipkaart = ovChipkaart;
    }

    public int getProductNummer() {
        return productNummer;
    }

    public void setProductNummer(int productNummer) {
        this.productNummer = productNummer;
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getBeschrijving() {
        return beschrijving;
    }

    public void setBeschrijving(String beschrijving) {
        this.beschrijving = beschrijving;
    }

    public float getPrijs() {
        return prijs;
    }

    public void setPrijs(float prijs) {
        this.prijs = prijs;
    }

    public List<OVChipkaart> getOvChipkaart() {
        return ovChipkaart;
    }

    public void setOvChipkaart(List<OVChipkaart> ovChipkaart) {
        this.ovChipkaart = ovChipkaart;
    }

    @Override
    public String toString() {
        return  '\n' + "Product:" +
                "productNummer =" + productNummer +
                ", naam ='" + naam + '\'' +
                ", beschrijving ='" + beschrijving + '\'' +
                ", prijs =" + prijs;
    }
}
