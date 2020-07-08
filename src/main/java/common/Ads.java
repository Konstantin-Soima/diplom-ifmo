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
    //объект телефона
    //дата размещения
    //контактные данные


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

    @Override
    public String toString() {
        return "Ads{" +
                "link='" + link + '\'' +
                ", name='" + name + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
