package com.taotao.cart.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.github.abel533.entity.Example;
import com.taotao.cart.bean.Item;
import com.taotao.cart.mapper.CartMapper;
import com.taotao.cart.pojo.Cart;

@Service
@Transactional
public class CartService {

    @Autowired
    private CartMapper cartMapper;

    @Autowired
    private ItemService itemService;

    /**
     * 添加商品到购物车，需要判断该商品是否存在购物车中，如果存在，数量相加，如果不存在，直接添加
     * 
     * @param itemId
     * @param userId
     */
    public void addItemToCart(Long itemId, Long userId) {
        Cart record = new Cart();
        record.setUserId(userId);
        record.setItemId(itemId);
        Cart cart = this.cartMapper.selectOne(record);
        if (null == cart) {
            // 不存在，直接添加
            cart = new Cart();
            cart.setItemId(itemId);
            cart.setUserId(userId);
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

            this.cartMapper.insert(cart);

        } else {
            cart.setNum(cart.getNum() + 1); // TODO 默认为1，留作作业完成
            cart.setUpdated(new Date());
            this.cartMapper.updateByPrimaryKeySelective(cart);
        }
    }

    public List<Cart> queryCartList(Long userId) {
        Example example = new Example(Cart.class);
        example.setOrderByClause("created DESC");
        example.createCriteria().andEqualTo("userId", userId);
        // TODO 分页暂时不做
        return this.cartMapper.selectByExample(example);
    }

    public void updateNum(Long itemId, Integer num, Long userId) {
        // 修改的数据
        Cart record = new Cart();
        record.setNum(num);
        record.setUpdated(new Date());

        // 修改的条件
        Example example = new Example(Cart.class);
        example.createCriteria().andEqualTo("userId", userId).andEqualTo("itemId", itemId);
        this.cartMapper.updateByExampleSelective(record, example);
    }

    public void delete(Long itemId, Long userId) {
        Cart record = new Cart();
        record.setUserId(userId);
        record.setItemId(itemId);
        this.cartMapper.delete(record);
    }

}
