package com.example.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.reggie.common.BaseContext;
import com.example.reggie.common.Result;
import com.example.reggie.entity.AddressBook;
import com.example.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("addressBook")
public class AddressBookController {

    @Autowired
    private AddressBookService addressBookService;

    @GetMapping("list")
    public Result<List<AddressBook>> listResult(AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());

        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(null != addressBook.getUserId(), AddressBook::getUserId, addressBook.getUserId());
        addressBookLambdaQueryWrapper.orderByDesc(AddressBook::getUpdateTime);

        List<AddressBook> addressBookList = addressBookService.list(addressBookLambdaQueryWrapper);
        return Result.success(addressBookList);
    }

    @GetMapping("/{id}")
    public Result<AddressBook> getByID(@PathVariable Long id) {
        log.info("ID: {}", id);
        AddressBook addressBook = addressBookService.getById(id);
        return Result.success(addressBook);
    }

    @PutMapping
    public Result<String> update(@RequestBody AddressBook addressBook) {
        log.info("接收到AddressBook的信息: {}", addressBook);
        addressBookService.updateById(addressBook);
        return Result.success("更新成功");
    }

    @PostMapping
    public Result<String> save(@RequestBody AddressBook addressBook) {
        log.info("接收到AddressBook的信息: {}", addressBook);
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return Result.success("保存成功");
    }

    @PutMapping("default")
    public Result<AddressBook> setDefault(@RequestBody AddressBook addressBook) {
        log.info("addressBook: {}", addressBook);
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault, 0);
        //SQL:update address_book set is_default = 0 where user_id = ?
        addressBookService.update(wrapper);

        addressBook.setIsDefault(1);
        //SQL:update address_book set is_default = 1 where id = ?
        addressBookService.updateById(addressBook);
        return Result.success(addressBook);
    }

    @GetMapping("default")
    public Result<AddressBook> getDefault() {
        LambdaQueryWrapper<AddressBook> addressBookLambdaQueryWrapper = new LambdaQueryWrapper<>();
        addressBookLambdaQueryWrapper.eq(AddressBook::getUserId, BaseContext.getCurrentId());
        addressBookLambdaQueryWrapper.eq(AddressBook::getIsDefault, 1);

        AddressBook addressBook = addressBookService.getOne(addressBookLambdaQueryWrapper);
        if(null == addressBook) {
            return Result.error("没有找到对象");
        }else {
            return Result.success(addressBook);
        }
    }
}
