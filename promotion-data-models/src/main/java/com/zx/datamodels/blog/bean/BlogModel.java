package com.zx.datamodels.blog.bean;

import java.io.Serializable;
import java.util.List;

import com.zx.datamodels.content.bean.BizFeed;
import com.zx.datamodels.content.bean.entity.Blog;
import com.zx.datamodels.content.bean.entity.Feed;
import com.zx.datamodels.user.bean.entity.User;

public class BlogModel implements Serializable {

    private Blog blog;

    private User user;

    private List<BizFeed> bizFeeds;
    
    private String openAccount;
    
    private String openAccountUrl;
    
    private List<Blog> blogs;
    
    public Blog getBlog() {
        return blog;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<BizFeed> getBizFeeds() {
        return bizFeeds;
    }

    public void setBizFeeds(List<BizFeed> bizFeeds) {
        this.bizFeeds = bizFeeds;
    }

    public String getOpenAccount() {
        return openAccount;
    }

    public void setOpenAccount(String openAccount) {
        this.openAccount = openAccount;
    }

    public String getOpenAccountUrl() {
        return openAccountUrl;
    }

    public void setOpenAccountUrl(String openAccountUrl) {
        this.openAccountUrl = openAccountUrl;
    }

    public List<Blog> getBlogs() {
        return blogs;
    }

    public void setBlogs(List<Blog> blogs) {
        this.blogs = blogs;
    }
}
