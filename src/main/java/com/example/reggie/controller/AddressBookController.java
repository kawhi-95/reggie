package com.example.reggie.controller;

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
    public Result<List<AddressBook>> listResult() {
        List<AddressBook> addressBookList = addressBookService.list();
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
}
