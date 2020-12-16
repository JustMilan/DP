package P6;

import javax.persistence.*;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ov_chipkaart")
public class OVChipkaart {

    @Id
    private int kaart_nummer;

    @Column
    private Date geldig_tot;
    private int klasse;
    private float saldo;
    private int reiziger_id;

    @ManyToOne
    // Caused by: org.hibernate.MappingException: Repeated column in mapping for entity: P6.OVChipkaart column: reiziger_id (should be mapped with insert="false" update="false")
    @JoinColumn(name = "reiziger_id", insertable = false, updatable = false)
    private Reiziger reiziger;

    @ManyToMany
    // https://stackoverflow.com/questions/38817417/what-is-the-meaning-of-hibernate-inversejoincolumns/38818049
    @JoinTable(name = "ov_chipkaart_product",
            joinColumns = {@JoinColumn(name = "kaart_nummer")},
            inverseJoinColumns = {@JoinColumn(name = "product_nummer")})
    private List<Product> producten = new ArrayList<>();

    public OVChipkaart() {
    }

    public OVChipkaart(int kaart_nummer, Date geldig_tot, int klasse, float saldo, int reiziger_id) {
        this.kaart_nummer = kaart_nummer;
        this.geldig_tot = geldig_tot;
        this.klasse = klasse;
        this.saldo = saldo;
        this.reiziger_id = reiziger_id;
    }

    public Reiziger getReiziger() {
        return reiziger;
    }

    public void setReiziger(Reiziger reiziger) {
        this.reiziger = reiziger;
    }

    public int getKaart_nummer() {
        return kaart_nummer;
    }

    public void setKaart_nummer(int kaart_nummer) {
        this.kaart_nummer = kaart_nummer;
    }

    public Date getGeldig_tot() {
        return geldig_tot;
    }

    public void setGeldig_tot(Date geldig_tot) {
        this.geldig_tot = geldig_tot;
    }

    public int getKlasse() {
        return klasse;
    }

    public void setKlasse(int klasse) {
        this.klasse = klasse;
    }

    public float getSaldo() {
        return saldo;
    }

    public void setSaldo(float saldo) {
        this.saldo = saldo;
    }

    @Override
    public String toString() {
        return "OVChipkaart " +
                kaart_nummer + ", " +
                geldig_tot + ", " +
                klasse + ", " +
                saldo + ", " +
                "reiziger_id {" + reiziger_id + "}, " +
                "Product(en) {" + producten + "} ";
    }
}
