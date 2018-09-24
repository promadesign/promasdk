package nl.martijnvandesande.promasdk;

/**
 * Created by martijn.vandersande on 1/4/17.
 */

public class PromaCrossPromotionsModel{
    public String icon;
    public String link;
    public String name;

    public PromaCrossPromotionsModel(String link, String name){
        this.link = link;
        this.name = name;
    }

    public PromaCrossPromotionsModel(String link, String name, String icon){
        this.icon = icon;
        this.link = link;
        this.name = name;
    }

}
