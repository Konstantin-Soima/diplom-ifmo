package common;

import java.io.Serializable;

public class Ads implements Serializable {
    //сериализация
    //ссылка
    private String link;
    //Название
    private String name;
    //фотки
    //текст
    private String content;
    //объект телефона (не телефон а data:image)
    private String phoneImg;
    //дата размещения
    //контактные данные
    private String contactName;
    private String contactLink;

    public String getPhoneImg() {
        return phoneImg;
    }

    public void setPhoneImg(String phoneImg) {
        this.phoneImg = phoneImg;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactLink() {
        return contactLink;
    }

    public void setContactLink(String contactLink) {
        this.contactLink = contactLink;
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

    public boolean isEmpty(){
        return link == null || link.isEmpty();

    }
    @Override
    public String toString() {
        return "Ads{" +
                "link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                ", image='" + phoneImg + '\'' +
                '}';
    }
}
