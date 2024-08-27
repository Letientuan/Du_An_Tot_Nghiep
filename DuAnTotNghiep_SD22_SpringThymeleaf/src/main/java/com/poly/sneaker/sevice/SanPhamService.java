package com.poly.sneaker.sevice;

import com.poly.sneaker.entity.SanPham;
import com.poly.sneaker.repository.SanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class SanPhamService {
    @Autowired
    private SanPhamRepository repository;

    public List<SanPham> getAll() {
        return repository.findAll();
    }

    public Page<SanPham> pagination(int pageNo){
        Pageable pageable = PageRequest.of(pageNo - 1, 10);
        return repository.findAll(pageable);
    }

    public List<SanPham> searchSanPham(String keyword){
        return repository.searchSP(keyword);
    }

    public Page<SanPham> searchSanPham(String keyword, Integer pageNo){
        List list = repository.searchSP(keyword);

        Pageable pageable = PageRequest.of(pageNo-1, 10);

        Integer start = (int) pageable.getOffset();

        Integer end = (int) ((pageable.getOffset() + pageable.getPageSize()) > list.size() ? list.size() : pageable.getOffset() + pageable.getPageSize());

        list = list.subList(start, end);

        return new PageImpl<SanPham>(list, pageable, searchSanPham(keyword).size());
    }

    public SanPham add(SanPham sanPham) {
        sanPham.setNgayTao(new Date());
        sanPham.setNgayCapNhat(null);
        sanPham.setNguoiTao("admin");
        return repository.save(sanPham);
    }

    public SanPham finById(Long id) {
        Optional<SanPham> optional = repository.findById(id);
        return optional.map(o -> o).orElse(null);
    }

    public SanPham deleteById(Long id) {
        Optional<SanPham> optional = repository.findById(id);
        return optional.map(o -> {
            repository.delete(o);
            return o;
        }).orElse(null);
    }

    public SanPham update(Long id, SanPham newSanPham) {
        Optional<SanPham> optional = repository.findById(id);
        return optional.map(o -> {
            o.setTen(newSanPham.getTen());
            o.setThuongHieu(newSanPham.getThuongHieu());
            o.setMoTa(newSanPham.getMoTa());
            o.setNgayTao(newSanPham.getNgayTao());
            o.setNgayCapNhat(newSanPham.getNgayCapNhat());
            o.setNguoiTao(newSanPham.getNguoiTao());
            o.setNguoiCapNhat(newSanPham.getNguoiCapNhat());
            o.setTrangThai(newSanPham.getTrangThai());

            return repository.save(o);
        }).orElse(null);
    }

    public SanPham updateTrangThai(Long id) {
        Optional<SanPham> optional = repository.findById(id);
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
