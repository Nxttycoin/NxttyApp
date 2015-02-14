package com.nxtapp.classes;

public class GroupDetailModel
{

    String id, title, body, url, 
    source, image, spamCount, deleted, 
    created, modified, nxtAccountId, 
    nameAlias, avatar, role, notification, blocked, spamCount_owner,
	    created_owner, blockedDate,
	    imagePath, commentCount, tipCount;
    
    boolean isOwner;

    public String getCommentCount()
    {
	return commentCount;
    }

    public String getTipCount()
    {
	return tipCount;
    }

    public void setTipCount(String tipCount)
    {
	this.tipCount = tipCount;
    }

    public void setCommentCount(String commentCount)
    {
	this.commentCount = commentCount;
    }

    public String getImagePath()
    {
	return imagePath;
    }

    public void setImagePath(String imagePath)
    {
	this.imagePath = imagePath;
    }

    

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

    public String getBody()
    {
	return body;
    }

    public String getUrl()
    {
	return url;
    }

    public String getSource()
    {
	return source;
    }

    public String getImage()
    {
	return image;
    }

    public String getSpamCount()
    {
	return spamCount;
    }

    public String getDeleted()
    {
	return deleted;
    }

    public String getCreated()
    {
	return created;
    }

    public String getModified()
    {
	return modified;
    }

    public String getNxtAccountId()
    {
	return nxtAccountId;
    }

    public String getNameAlias()
    {
	return nameAlias;
    }

    public String getAvatar()
    {
	return avatar;
    }

    public String getRole()
    {
	return role;
    }

    public String getNotification()
    {
	return notification;
    }

    public String getBlocked()
    {
	return blocked;
    }

    public String getSpamCount_owner()
    {
	return spamCount_owner;
    }

    public String getCreated_owner()
    {
	return created_owner;
    }

    public String getBlockedDate()
    {
	return blockedDate;
    }

    public void setId(String id)
    {
	this.id = id;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

    public void setBody(String body)
    {
	this.body = body;
    }

    public void setUrl(String url)
    {
	this.url = url;
    }

    public void setSource(String source)
    {
	this.source = source;
    }

    public void setImage(String image)
    {
	this.image = image;
    }

    public void setSpamCount(String spamCount)
    {
	this.spamCount = spamCount;
    }

    public void setDeleted(String deleted)
    {
	this.deleted = deleted;
    }

    public void setCreated(String created)
    {
	this.created = created;
    }

    public void setModified(String modified)
    {
	this.modified = modified;
    }

    public void setNxtAccountId(String nxtAccountId)
    {
	this.nxtAccountId = nxtAccountId;
    }

    public void setNameAlias(String nameAlias)
    {
	this.nameAlias = nameAlias;
    }

    public void setAvatar(String avatar)
    {
	this.avatar = avatar;
    }

    public void setRole(String role)
    {
	this.role = role;
    }

    public void setNotification(String notification)
    {
	this.notification = notification;
    }

    public void setBlocked(String blocked)
    {
	this.blocked = blocked;
    }

    public void setSpamCount_owner(String spamCount_owner)
    {
	this.spamCount_owner = spamCount_owner;
    }

    public void setCreated_owner(String created_owner)
    {
	this.created_owner = created_owner;
    }

    public void setBlockedDate(String blockedDate)
    {
	this.blockedDate = blockedDate;
    }

}
