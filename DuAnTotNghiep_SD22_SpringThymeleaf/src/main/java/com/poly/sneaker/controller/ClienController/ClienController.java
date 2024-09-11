package com.poly.sneaker.controller.ClienController;

import com.poly.sneaker.dto.SanPhamBanChayDTO;
import com.poly.sneaker.entity.*;
import com.poly.sneaker.repository.*;
import com.poly.sneaker.sevice.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Controller
public class ClienController {

    @Autowired
    private SanPhamChiTietRepository repo;
    @Autowired
    private SanPhamChiTietService SPCTservice;

    @Autowired
    private HoaDonRepository hoaDonRepository;
    @Autowired
    private HoaDonChiTietRepository hdctrepo;
    @Autowired
    GioHangChiTietService  gioHangChiTietService;
    @Autowired
    private GioHangRepository ghrepo;
    @Autowired
    GiohangService giohangService;

    @Autowired
    GioHangChiTietRepository gioHangChiTietRepository;


    @GetMapping("/Client")
    public String hienThiSanPham(Model model) {
        List<SanPhamChiTiet> sanPhams = SPCTservice.getAll();
        model.addAttribute("sanPhams", sanPhams);
        return "client/viewClient";
    }
    @GetMapping("/giohangNoLogin")
    public String giohangNoLogin(Model model) {

        return "client/giohangNoLogin";
    }
    @GetMapping("/TraCuuDonHang")
    public String TraCuuDonHang(Model model) {
        return "client/TraCuuDonHang";
    }
    @GetMapping("/shop")
    public String hienThiSanPhamall(Model model) {
        List<SanPhamChiTiet> sanPhams = SPCTservice.getAll();
        Map<Long, SanPhamChiTiet> lstsp = new HashMap<>();
        for (SanPhamChiTiet spct : sanPhams) {
            Long idSanPham = spct.getSanPham().getId();
            lstsp.put(idSanPham, spct);
        }
        List<SanPhamChiTiet> uniqueSanPhams = new ArrayList<>(lstsp.values());
       
        model.addAttribute("sanPhams", uniqueSanPhams);
        return "client/shop";
    }
    @GetMapping("/san-pham/{sanPhamId}")
    public String hienThiSanPham (@PathVariable Long sanPhamId ,Model model) {
        SanPhamChiTiet sanPhamChiTiet = SPCTservice.findById1(sanPhamId);
        model.addAttribute("sanPhams", sanPhamChiTiet);
        return "client/SanPhamChiTiet";
    }

    @GetMapping("/sanphamchitietclien/{sanPhamId}")
    @ResponseBody
    public ResponseEntity<?> getProductDetails(@PathVariable Long sanPhamId) {
        SanPhamChiTiet sanPhamChiTiet = SPCTservice.findById1(sanPhamId);
        if (sanPhamChiTiet != null) {
            return ResponseEntity.ok(sanPhamChiTiet);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/sanphamchitietclienkc/{sanPhamId}")
    @ResponseBody
    public ResponseEntity<List<String>> getKichCo(@PathVariable Long sanPhamId) {
        List<String> kichCoList = repo.findKichCoBySanPhamId(sanPhamId);

        if (!kichCoList.isEmpty()) {
            return ResponseEntity.ok(kichCoList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/sanphamchitietclientanh/{sanPhamId}")
    @ResponseBody
    public ResponseEntity<List<String>> getanh(@PathVariable Long sanPhamId) {
        List<String> kichCoList = repo.findanhBySanPhamId(sanPhamId);
        Set<String> uniqueKichCoSet = new HashSet<>(kichCoList);
        List<String> uniqueKichCoList = new ArrayList<>(uniqueKichCoSet);
        System.out.println(uniqueKichCoList);
        if (!kichCoList.isEmpty()) {
            return ResponseEntity.ok(uniqueKichCoList);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/kichco/{idSanPham}")
    public ResponseEntity<List<String>> getDetails(
            @PathVariable Long idSanPham,
            @RequestParam String anh

    ) {
        String decodedAnh = URLDecoder.decode(anh, StandardCharsets.UTF_8);
        System.out.println(decodedAnh);
        List<String> details = repo.findKichCoBySanPhamIdAndImageName(idSanPham, decodedAnh);
        return ResponseEntity.ok(details);
    }

    @GetMapping("/sanphamchitietclientanhspct/{anh}/{idSanPham}")
    @ResponseBody
    public ResponseEntity<List<Long>> getanhspct(@PathVariable Long anh,
    @PathVariable Long idSanPham) {
        List<Long> list = repo.findIdsByAnhContaining(anh,idSanPham);

        if (!list.isEmpty()) {
            return ResponseEntity.ok(list);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    @GetMapping("/gia-ban/{idSanPham}/{ten}")
    @ResponseBody
    public List<Double> getGiaBan(
            @PathVariable Long idSanPham,
            @PathVariable String ten,
            @RequestParam String anh) {
        String decodedAnh = URLDecoder.decode(anh, StandardCharsets.UTF_8);
        return repo.findGiaBan(idSanPham, ten, decodedAnh);
    }
    @GetMapping("/SLton/{idSanPham}/{size}")
    @ResponseBody
    public List<Integer> getSL(
            @PathVariable Long idSanPham,
            @PathVariable String size,
            @RequestParam String anh) {
        String decodedAnh = URLDecoder.decode(anh, StandardCharsets.UTF_8);
        return repo.findSoLuong(idSanPham, decodedAnh,size );
    }


    @GetMapping("/san-pham-moi-nhat")
    public ResponseEntity<Map<String, Object>> getTop12NewestProducts(Pageable pageable) {
        List<SanPhamChiTiet> sanPhams = SPCTservice.getAll();

        Map<Long, SanPhamChiTiet> lstsp = new HashMap<>();
        for (SanPhamChiTiet spct : sanPhams) {
            Long idSanPham = spct.getSanPham().getId();
            lstsp.put(idSanPham, spct);
        }

        List<SanPhamChiTiet> uniqueSanPhams = new ArrayList<>(lstsp.values());
        LocalDate today = LocalDate.now();
        LocalDate oneMonthAgo = today.minusMonths(1);

        List<SanPhamChiTiet> filteredSanPhams = uniqueSanPhams.stream()
                .filter(sp -> {
                    LocalDate ngayTao = convertToLocalDate(sp.getSanPham().getNgayTao());
                    return !ngayTao.isBefore(oneMonthAgo) && !ngayTao.isAfter(today);
                })
                .collect(Collectors.toList());


        Map<String, Object> response = new HashMap<>();
        response.put("sanPham", filteredSanPhams);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private LocalDate convertToLocalDate(Date date) {
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    @GetMapping("/san-pham-ban-chay")
    public ResponseEntity<Map<String, Object>> getTop12SanPhamBanChay(Pageable pageable) {
        List<SanPhamBanChayDTO> spmn = hoaDonRepository.getSanPhamBanChayNhat(pageable);

        Map<String, Object> response = new HashMap<>();
        response.put("sanPham", spmn);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    @GetMapping("/detailcheckgh/{idkh}/{size}/{idsp}/{sl}")
public ResponseEntity<Map<String, Object>> detail(@PathVariable("idkh") Long idkh,
                                                  @RequestParam String anhlayve,
                                                  @PathVariable("size") String size,
                                                  @PathVariable("idsp") Long idsp,
                                                  @PathVariable("sl") Integer sl) {

    GioHang gh = giohangService.detail(idkh);
    String anh = URLDecoder.decode(anhlayve, StandardCharsets.UTF_8);
    if (gh == null) {
        GioHang ghnew = new GioHang();
        KhachHang kh = new KhachHang();
        kh.setId(idkh);
        ghnew.setKhachHang(kh);
        ghrepo.save(ghnew);

        gh = giohangService.detail(idkh);
    }


    GioHangChiTiet gioHangChiTiet = new GioHangChiTiet();
    gioHangChiTiet.setGioHang(gh);
    gioHangChiTiet.setSoLuong(sl);
    SanPhamChiTiet sp = repo.findidspct(idsp, anh, size);
    Long checkgh=ghrepo.selectIDGH(idkh);
    List<SanPhamChiTiet> checksp =gioHangChiTietRepository.findDetailsBySanPhamIdAndGioHangId(sp.getId(),checkgh);
    System.out.println(checksp);
    if(checksp.isEmpty()){
            gioHangChiTiet.setSanPhamChiTiet(sp);
            gioHangChiTietService.add(gioHangChiTiet);
    }
    else {
        Optional<GioHangChiTiet> ghct = gioHangChiTietRepository.findBySanPhamChiTietId(sp.getId());

        if (ghct.isPresent()) {
            GioHangChiTiet existingGioHangChiTiet = ghct.get();
            Integer slmoi = existingGioHangChiTiet.getSoLuong() + sl;
            gioHangChiTietService.updateSL(sp.getId(), slmoi);
        } else {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "GioHangChiTiet not found"));
        }

    }
    Map<String, Object> response = new HashMap<>();
    response.put("giỏ_hàng", gh);
    return ResponseEntity.ok(response);
}
    @GetMapping("/san-pham-lienquan/{ten}")
    public ResponseEntity<Map<String, Object>> getSanPhamByThuongHieu(@PathVariable("ten") String ten) {
        List<SanPhamChiTiet> sanPhamList = repo.findByThuongHieu_TenThuongHieu(ten);
        Map<Long, SanPhamChiTiet> lstsp = new HashMap<>();
        for (SanPhamChiTiet spct : sanPhamList) {
            Long idSanPham = spct.getSanPham().getId();
            lstsp.put(idSanPham, spct);
        }

        List<SanPhamChiTiet> uniqueSanPhams = new ArrayList<>(lstsp.values());
        Map<String, Object> response = new HashMap<>();
        response.put("sanPham", uniqueSanPhams);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping("/tra-cuu-hoa-don-chi-tiet/{ma}")
    public ResponseEntity<List<HoaDonChiTiet>> getHoaDonChiTietByMa(@PathVariable String ma) {
        List<HoaDonChiTiet> hoaDonChiTietList = hdctrepo.findByMa(ma);
        if (hoaDonChiTietList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(hoaDonChiTietList, HttpStatus.OK);
    }
    @GetMapping("/tra-cuu-hoa-don/{ma}")
    public ResponseEntity<List<HoaDon>> getHoaDonByMa(@PathVariable String ma) {
        List<HoaDon> hoaDonChiTietList = hoaDonRepository.findByMa(ma);
        if (hoaDonChiTietList.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(hoaDonChiTietList, HttpStatus.OK);
    }
    @GetMapping("/checksp/{size}/{idsp}")
    public ResponseEntity<SanPhamChiTiet> checksp(@PathVariable("size") String size,
                                                  @PathVariable("idsp") Long idsp,
                                                  @RequestParam String anhlayve) {

        String anh = URLDecoder.decode(anhlayve, StandardCharsets.UTF_8);
        SanPhamChiTiet sp = repo.findidspct(idsp, anh, size);
        if (sp == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(sp, HttpStatus.OK);
    }


}
