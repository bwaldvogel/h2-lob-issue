package de.bwaldvogel;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;

@Entity
public class LobEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Lob
    private Serializable lob;

    public Long getId() {
        return id;
    }

    public void setLob(Serializable lob) {
        this.lob = lob;
    }

    public Serializable getLob() {
        return lob;
    }
}