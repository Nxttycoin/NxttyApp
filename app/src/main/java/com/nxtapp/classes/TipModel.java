package com.nxtapp.classes;

public class TipModel
{
    String Name, Id, TipValue, ImagePath, TipTimestamp, TipCount;

    public String getTipCount()
    {
	return TipCount;
    }

    public void setTipCount(String tipCount)
    {
	TipCount = tipCount;
    }

    public String getTipTimestamp()
    {
	return TipTimestamp;
    }

    public void setTipTimestamp(String tipTimestamp)
    {
	TipTimestamp = tipTimestamp;
    }

    public TipModel()
    {
    }

    public TipModel(String name, String id, String tipvalue, String imagePath)
    {
	this.Name = name;
	this.Id = id;
	this.TipValue = tipvalue;
	this.ImagePath = imagePath;

    }

    public String getName()
    {
	return Name;
    }

    public void setName(String name)
    {
	Name = name;
    }

    public String getId()
    {
	return Id;
    }

    public void setId(String id)
    {
	Id = id;
    }

    public String getTipValue()
    {
	return TipValue;
    }

    public void setTipValue(String tipValue)
    {
	TipValue = tipValue;
    }

    public String getImagePath()
    {
	return ImagePath;
    }

    public void setImagePath(String imagePath)
    {
	ImagePath = imagePath;
    }

}
