package com.taotao.cart.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taotao.cart.bean.Item;
import com.taotao.cart.pojo.Cart;
import com.taotao.common.utils.CookieUtils;

@Service
public class CartCookieService {

    public static final String COOKIE_NAME = "TT_CART";

    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Autowired
    private ItemService itemService;

    public List<Cart> queryCartList(HttpServletRequest request) {
        try {
            String jsonData = CookieUtils.getCookieValue(request, COOKIE_NAME, true);
            if (StringUtils.isNotEmpty(jsonData)) {
                return MAPPER.readValue(jsonData,
                        MAPPER.getTypeFactory().constructCollectionType(List.class, Cart.class));
                // TODO 对集合做排序，按照创建时间倒序排序
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<Cart>(0);
    }

    public void addItemToCart(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = this.queryCartList(request);
        Cart cart = null;
        for (Cart c : carts) {
            if (c.getItemId().longValue() == itemId.longValue()) {
                cart = c;
                break;
            }
        }

        if (null == cart) {
            // 不存在，直接添加
            cart = new Cart();
            cart.setItemId(itemId);
            cart.setCreated(new Date());
            cart.setUpdated(cart.getCreated());
            cart.setNum(1); // TODO 默认为1
            Item item = this.itemService.queryItemById(itemId);
            if (null == item) {
                // TODO 待完善
                return;
            }
            cart.setItemImage(item.getImages()[0]); // TODO 待完善
            cart.setItemPrice(item.getPrice());
            cart.setItemTitle(item.getTitle());

            carts.add(cart);

        } else {
            cart.setNum(cart.getNum() + 1); // TODO 默认为1，留作作业完成
            cart.setUpdated(new Date());
        }

        try {
            // 将数据写入到cookie中
            CookieUtils.setCookie(request, response, COOKIE_NAME, MAPPER.writeValueAsString(carts), 60 * 60
                    * 24 * 30 * 12, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateNum(Long itemId, Integer num, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = this.queryCartList(request);
        Cart cart = null;
        for (Cart c : carts) {
            if (c.getItemId().longValue() == itemId.longValue()) {
                cart = c;
                break;
            }
        }

        if (null == cart) {
            return;
        }

        cart.setNum(num);
        cart.setUpdated(new Date());

        try {
            // 将数据写入到cookie中
            CookieUtils.setCookie(request, response, COOKIE_NAME, MAPPER.writeValueAsString(carts), 60 * 60
                    * 24 * 30 * 12, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void delete(Long itemId, HttpServletRequest request, HttpServletResponse response) {
        List<Cart> carts = this.queryCartList(request);
        Cart cart = null;
        for (Cart c : carts) {
            if (c.getItemId().longValue() == itemId.longValue()) {
                cart = c;
                carts.remove(c);
                break;
            }
        }

        if (null != cart) {
            try {
                // 将数据写入到cookie中
                CookieUtils.setCookie(request, response, COOKIE_NAME, MAPPER.writeValueAsString(carts), 60
                        * 60 * 24 * 30 * 12, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
