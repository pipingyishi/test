package com.zx.frontend.promotion.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mobile.device.Device;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.zx.datamodels.blog.bean.BlogModel;
import com.zx.datamodels.common.request.PagingRequest;
import com.zx.datamodels.content.bean.BizFeed;
import com.zx.datamodels.content.bean.entity.Blog;
import com.zx.datamodels.content.bean.entity.Dict;
import com.zx.datamodels.content.constants.ContentTypeDef;
import com.zx.datamodels.user.bean.entity.User;
import com.zx.frontend.promotion.property.util.Property;
import com.zx.frontend.promotion.util.DigitUtil;
import com.zx.modules.content.service.BlogService;
import com.zx.modules.content.service.DictService;
import com.zx.modules.content.service.FeedService;

@Controller
@RequestMapping(value = "blog")
public class BlogController {

    private static final int PAGE_NUM = 0;

    private static final int PAGE_SIZE = 10;

    private static final String PROPERTY_NAME = "url.properties";

    private static final String PROPERTY = "open-account.properties";

    private static final String OPEN_ACCOUNT = "blog_open_account";

    private static final String OPEN_ACCOUNT_URL = "blog_open_account_url";

    private static final String BLOG_LIST = "pages/blog/blog-list::blog";

    @Autowired
    private BlogService blogService;

    @Autowired
    private FeedService feedService;

    @Autowired
    private DictService dictService;

    /**
     * 拉取资讯信息
     * 
     * @param request
     * @param userId
     * @param blogId
     * @param model
     */
    @RequestMapping(value = "/{blogId}")
    public String blog(@PathVariable("blogId") String blogId, ModelMap model, Device device) {
        boolean isMobile = device.isMobile();
        if (isMobile) {
            if (!DigitUtil.isAllDigit(blogId)) {
                return "/resources/error";
            }
            BlogModel blogModel = new BlogModel();
            Blog blog = blogService.findById(Long.parseLong(blogId));
            if (blog == null) {
                return "/resources/error";
            }
            User user = blog.getUser();
            blogModel.setUser(user);
            blogModel.setBlog(blog);
            List<BizFeed> bizFeeds = feedService.findBizFeedsForObject(null, ContentTypeDef.BLOG, blogId, PAGE_NUM,
                    PAGE_SIZE);
            blogModel.setBizFeeds(bizFeeds);
            blogModel = (BlogModel) Property.getOpenAccount(PROPERTY, blogModel, OPEN_ACCOUNT, OPEN_ACCOUNT_URL);
            model.put("blogModel", blogModel);
            return "pages/blog/blog";
        } else {
            String url = Property.getUrl(PROPERTY_NAME, "blogId", blogId);
            if (url == null) {
                return "/resources/error";
            } else {
                return url;
            }
        }
    }

    @RequestMapping(value = "")
    public String information(ModelMap model) {
        List<Dict> dicts = dictService.findDicts("blogType");
        model.put("dicts", dicts);
        return "pages/blog/information";
    }

    @RequestMapping(value = "/pull/{type}/{pageNum}")
    public String findBlog(@PathVariable("type") int type, @PathVariable("pageNum") int pageNum, ModelMap model) {
        BlogModel blogModel = new BlogModel();
        if (pageNum != 1) {// 只查列表
            List<Blog> blogs = blogService.findOpenBlogs(pageNum, PagingRequest.PAGE_SIZE, type, null);
            blogModel.setBlogs(blogs);
        } else {
            Blog blog = blogService.findTopBlog(type);
            List<Blog> blogs = blogService.findOpenBlogs(pageNum, PagingRequest.PAGE_SIZE, type, null);
            blogModel.setBlog(blog);
            blogModel.setBlogs(blogs);
        }
        model.put("blogModel", blogModel);
        return BLOG_LIST;
    }
}
