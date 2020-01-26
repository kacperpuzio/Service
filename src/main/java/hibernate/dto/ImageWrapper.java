package hibernate.dto;


import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "icon")
public class ImageWrapper implements Serializable {

    private static final long serialVersionUID = 1L;

    public ImageWrapper() {
    }

    public ImageWrapper(int id, String imageName){
        this.id = id;
        this.imageName = imageName;
    }
    public ImageWrapper(int id, String imageName, byte[] imageData){
        this.id = id;
        this.imageName = imageName;
        this.imageData = imageData;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Integer id;

    @Column(name = "imageName", unique = false, nullable = false, length = 100)
    private String imageName;

    @Column(name = "imageData", unique = false, nullable = false, length = 100000)
    private byte[] imageData;

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public byte[] getImageData() {
        return imageData;
    }
    public void setImageData(byte[] imageData) {
        this.imageData = imageData;
    }

    public String getImageName() {
        return imageName;
    }
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

}
