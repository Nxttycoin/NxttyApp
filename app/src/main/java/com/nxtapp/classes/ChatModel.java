package com.nxtapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class ChatModel implements Parcelable
{

    String Id, senderId, receiverId, body, createdDate, read, image, seenDate, From, Content, ImagePath;

    public ChatModel()
    {
	// TODO Auto-generated constructor stub
    }

    public ChatModel(Parcel in)
    {
	// TODO Auto-generated constructor stub
	Id = in.readString();
	senderId = in.readString();
	receiverId = in.readString();
	body = in.readString();
	createdDate = in.readString();
	read = in.readString();
	image = in.readString();
	seenDate = in.readString();
	From = in.readString();
	Content = in.readString();
	ImagePath = in.readString();
    }
    
    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
	// TODO Auto-generated method stub
	dest.writeString(Id);
	dest.writeString(senderId);
	dest.writeString(receiverId);
	dest.writeString(body);
	dest.writeString(createdDate);
	dest.writeString(read);
	dest.writeString(image);
	dest.writeString(seenDate);
	dest.writeString(From);
	dest.writeString(Content);
	dest.writeString(ImagePath);
    }

    // Bitmap ImageBitmap;

    // public Bitmap getImageBitmap() {
    // return ImageBitmap;
    // }
    //
    // public void setImageBitmap(Bitmap imageBitmap) {
    // ImageBitmap = imageBitmap;
    // }

    public String getImagePath()
    {
	return ImagePath;
    }

    public void setImagePath(String imagePath)
    {
	ImagePath = imagePath;
    }

    // public String getImageBase64()
    // {
    // return imageBase64;
    // }
    //
    // public void setImageBase64(String imageBase64)
    // {
    // this.imageBase64 = imageBase64;
    // }

    public String getContent()
    {
	return Content;
    }

    public void setContent(String content)
    {
	Content = content;
    }

    public String getFrom()
    {
	return From;
    }

    public void setFrom(String from)
    {
	From = from;
    }

    public String getId()
    {
	return Id;
    }

    public String getSenderId()
    {
	return senderId;
    }

    public String getReceiverId()
    {
	return receiverId;
    }

    public String getBody()
    {
	return body;
    }

    public String getCreatedDate()
    {
	return createdDate;
    }

    public String getRead()
    {
	return read;
    }

    public String getImage()
    {
	return image;
    }

    public String getSeenDate()
    {
	return seenDate;
    }

    public Long getSeenDateLong()
    {
	try
	{
	    return Long.parseLong(createdDate);
	} catch (Exception e)
	{
	    // TODO: handle exception
	}

	return Long.valueOf(0);

    }

    public void setId(String id)
    {
	Id = id;
    }

    public void setSenderId(String senderId)
    {
	this.senderId = senderId;
    }

    public void setReceiverId(String receiverId)
    {
	this.receiverId = receiverId;
    }

    public void setBody(String body)
    {
	this.body = body;
    }

    public void setCreatedDate(String createdDate)
    {
	this.createdDate = createdDate;
    }

    public void setRead(String read)
    {
	this.read = read;
    }

    public void setImage(String image)
    {
	this.image = image;
    }

    public void setSeenDate(String seenDate)
    {
	this.seenDate = seenDate;
    }

    public static final Parcelable.Creator<ChatModel> CREATOR = new Parcelable.Creator<ChatModel>()
    {
	public ChatModel createFromParcel(Parcel in)
	{
	    return new ChatModel(in);
	}

	public ChatModel[] newArray(int size)
	{
	    return new ChatModel[size];
	}
    };

    @Override
    public int describeContents()
    {
	// TODO Auto-generated method stub
	return 0;
    }

   

}
