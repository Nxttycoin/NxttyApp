package com.nxtapp.classes;

public class CommentModel
{

    String Image, Id, Name, Timestamp, Body, CommentID;

    public String getCommentID()
    {
	return CommentID;
    }

    public void setCommentID(String commentID)
    {
	CommentID = commentID;
    }

    public String getImage()
    {
	return Image;
    }

    public String getId()
    {
	return Id;
    }

    public String getName()
    {
	return Name;
    }

    public String getTimestamp()
    {
	return Timestamp;
    }

    public String getBody()
    {
	return Body;
    }

    public void setImage(String image)
    {
	Image = image;
    }

    public void setId(String id)
    {
	Id = id;
    }

    public void setName(String name)
    {
	Name = name;
    }

    public void setTimestamp(String timestamp)
    {
	Timestamp = timestamp;
    }

    public void setBody(String body)
    {
	Body = body;
    }

}
