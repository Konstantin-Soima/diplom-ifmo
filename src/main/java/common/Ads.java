package common;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;

public class Ads implements Serializable {
    //сериализация
    private int id;
    //ссылка
    private String link;
    //Название
    private String name;
    //фотки
    //текст
    private String content;
    //объект телефона (не телефон а data:image)
    private String phone;
    //дата размещения
    private Date date;
    //контактные данные
    private Profile profile;

    public String getPhone() { return phone; }

    public void setPhone(String phone) {
        this.phone = phone;
    }


    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getDate() { return date; }

    public void setDate(Date date) { this.date = date; }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public Profile getProfile() { return profile; }

    public void setProfile(Profile profile) { this.profile = profile; }

    public Ads() {
        Date dateTime = new  Date();
        this.date = dateTime;
    }
    @JsonIgnore
    public boolean isEmpty(){
        return link == null || link.isEmpty();

    }
    @Override
    public String toString() {
        return "Ads{" +
                "link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", image='" + phone + '\'' +
                '}';
    }
}
