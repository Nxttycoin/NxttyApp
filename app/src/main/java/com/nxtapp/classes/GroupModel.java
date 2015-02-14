package com.nxtapp.classes;

import android.os.Parcel;
import android.os.Parcelable;

public class GroupModel implements Parcelable
{

    String id, title,
    description, category, 
    avatar, created, modified, posts, members,deleted;

    
    
    public GroupModel(String id, String title, String description, String category, String avatar, String created, String modified, String posts, String members, String deleted,
	    boolean isOwner)
    {
	super();
	this.id = id;
	this.title = title;
	this.description = description;
	this.category = category;
	this.avatar = avatar;
	this.created = created;
	this.modified = modified;
	this.posts = posts;
	this.members = members;
	this.deleted = deleted;
	this.isOwner = isOwner;
    }

    public String getDeleted()
    {
        return deleted;
    }

    public void setDeleted(String deleted)
    {
        this.deleted = deleted;
    }

    boolean isOwner;

    public boolean isOwner()
    {
	return isOwner;
    }

    public void setOwner(boolean isOwner)
    {
	this.isOwner = isOwner;
    }

    public String getId()
    {
	return id;
    }

    public String getTitle()
    {
	return title;
    }

    public String getDescription()
    {
	return description;
    }

    public String getCategory()
    {
	return category;
    }

    public String getAvatar()
    {
	return avatar;
    }

    public String getCreated()
    {
	return created;
    }

    public String getModified()
    {
	return modified;
    }

    public String getPosts()
    {
	return posts;
    }

    public String getMembers()
    {
	return members;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public void setDescription(String description)
    {
	this.description = description;
    }

    public void setCategory(String category)
    {
	this.category = category;
    }

    public void setAvatar(String avatar)
    {
	this.avatar = avatar;
    }

    public void setCreated(String created)
    {
	this.created = created;
    }

    public void setModified(String modified)
    {
	this.modified = modified;
    }

    public void setPosts(String posts)
    {
	this.posts = posts;
    }

    public void setMembers(String members)
    {
	this.members = members;
    }

    public GroupModel(Parcel in)
    {
	id = in.readString();
	title = in.readString();
	description = in.readString();
	category = in.readString();
	avatar = in.readString();
	created = in.readString();
	modified = in.readString();
	posts = in.readString();
	members = in.readString();
    }

    public GroupModel()
    {
	// TODO Auto-generated constructor stub
    }

    public static final Parcelable.Creator<GroupModel> CREATOR = new Parcelable.Creator<GroupModel>()
    {
	@Override
	public GroupModel createFromParcel(Parcel source)
	{
	    // TODO Auto-generated method stub
	    return new GroupModel(source);
	}

	@Override
	public GroupModel[] newArray(int size)
	{
	    // TODO Auto-generated method stub
	    return new GroupModel[size];
	}
    };

    @Override
    public void writeToParcel(Parcel dest, int flags)
    {
	dest.writeString(id);
	dest.writeString(title);
	dest.writeString(description);
	dest.writeString(category);
	dest.writeString(avatar);
	dest.writeString(created);
	dest.writeString(modified);
	dest.writeString(posts);
	dest.writeString(members);
    }

    @Override
    public int describeContents()
    {
	// TODO Auto-generated method stub
	return 0;
    }

}
