package lu.pata.stromae.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Getter
@Setter
@EqualsAndHashCode
@Entity
@ToString
public class FileSync {
    @Id
    @GeneratedValue
    @Column(name = "FILE_ID")
    private Long Id;

    private String name;
    private Long size;
    private String hash;
    private String owner;

    public FileSync(String name, String owner) {
        this.name = name;
        this.owner = owner;
        this.hash="N/A";
        this.size=0l;
    }

    public FileSync() {
    }
}
