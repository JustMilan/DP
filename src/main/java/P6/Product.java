package P6;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
public class Product {

    @Id
    private int product_nummer;

    @Column
    private String naam;
    private String beschrijving;
    private float prijs;

    public Product() {}

    public Product(int product_nummer, String naam, String beschrijving, float prijs) {
        this.product_nummer = product_nummer;
        this.naam = naam;
        this.beschrijving = beschrijving;
        this.prijs = prijs;
    }

    @ManyToMany(mappedBy = "producten", targetEntity = OVChipkaart.class)
    private List<OVChipkaart> ovChipkaarten = new ArrayList<>();

    public int getProduct_nummer() {
        return product_nummer;
    }

    public void setProduct_nummer(int product_nummer) {
        this.product_nummer = product_nummer;
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

    @Override
    public String toString() {
        return "Product " +
                product_nummer + ", " +
                naam + ", " +
                beschrijving + ", " +
                prijs;
    }
}
