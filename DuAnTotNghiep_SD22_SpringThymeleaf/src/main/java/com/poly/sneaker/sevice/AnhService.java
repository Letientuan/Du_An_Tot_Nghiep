package com.poly.sneaker.sevice;

import com.poly.sneaker.entity.Anh;
import com.poly.sneaker.entity.SanPhamChiTiet;
import com.poly.sneaker.repository.AnhRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AnhService {
    @Autowired
    private AnhRepository repository;

    public List<Anh> getAll() {
        return repository.findAll();
    }

    public Anh save(Anh anh) {
        return repository.save(anh);
    }

    public List<Anh> addAll(List<Anh> anhList) {
        return repository.saveAll(anhList);
    }

    public Anh finById(Long id) {
        Optional<Anh> optional = repository.findById(id);
        return optional.map(o -> o).orElse(null);
    }

    public Anh deleteById(Long id) {
        Optional<Anh> optional = repository.findById(id);
        return optional.map(o -> {
            repository.delete(o);
            return o;
        }).orElse(null);
    }

    public Anh update(Long id, Anh newAnh) {
        Optional<Anh> optional = repository.findById(id);
        return optional.map(o -> {
            o.setTen(newAnh.getTen());
            o.setUrl(newAnh.getUrl());
            o.setNgayTao(newAnh.getNgayTao());
            o.setNgayCapNhat(newAnh.getNgayCapNhat());
            o.setNguoiTao(newAnh.getNguoiTao());
            o.setNguoiCapNhat(newAnh.getNguoiCapNhat());
            o.setTrangThai(newAnh.getTrangThai());

            return repository.save(o);
        }).orElse(null);
    }

    public Anh updateTrangThai(Long id) {
        Optional<Anh> optional = repository.findById(id);
        return optional.map(o -> {
            o.setTrangThai(0);
            return repository.save(o);
        }).orElse(null);
    }

    public Boolean existingById(Long id) {
        return repository.existsById(id);
    }

    public Boolean existingByTen(String ten) {
        return repository.findByTen(ten).size() > 0;
    }

}
