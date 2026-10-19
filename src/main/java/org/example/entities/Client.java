package org.example.entities;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "clients")
public class Client extends User {

    @Column(name = "date_inscription")
    private LocalDate dateInscription;

    public Client() {}

    public Client(String nom) {
        super(nom);
        this.dateInscription = LocalDate.now();
    }

    public LocalDate getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDate dateInscription) { this.dateInscription = dateInscription; }

    @Override
    public String toString() {
        return "Client{id=" + getId() + ", nom='" + getNom() + "'}";
    }
}