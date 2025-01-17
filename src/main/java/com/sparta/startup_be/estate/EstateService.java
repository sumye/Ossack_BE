package com.sparta.startup_be.estate;

import com.sparta.startup_be.estate.dto.CityResponseDto;
import com.sparta.startup_be.estate.dto.EstateResponseDto;
import com.sparta.startup_be.estate.dto.MapResponseDto;
import com.sparta.startup_be.estate.dto.SearchDto;
import com.sparta.startup_be.model.CoordinateEstate;
import com.sparta.startup_be.coordinate.service.CoordinateEstateService;
import com.sparta.startup_be.coordinate.dto.CoordinateResponseDto;
import com.sparta.startup_be.model.Estate;
import com.sparta.startup_be.model.Favorite;
import com.sparta.startup_be.favorite.FavoriteRepository;
import com.sparta.startup_be.login.repository.UserRepository;
import com.sparta.startup_be.login.security.UserDetailsImpl;
import com.sparta.startup_be.login.model.User;
import com.sparta.startup_be.utils.NaverSearchApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import com.sparta.startup_be.coordinate.repository.CoordinateEstateRepository;
import com.sparta.startup_be.utils.ConvertAddress;

import java.util.*;


@RequiredArgsConstructor
@Service
public class EstateService {
    private final EstateRepository estateRepository;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;
    private final CoordinateEstateRepository coordinateEstateRepository;
    private final ConvertAddress convertAddress;
    private final CoordinateEstateService coordinateEstateService;
    private final NaverSearchApi naverSearchApi;

    public void storeEstate(List<Estate> estates) {
        coordinateEstateService.storeAddress(estates);
        for (Estate estate : estates) {
            estateRepository.save(estate);
        }
    }
    //메인페이지 해당 동 조회
    public List<EstateResponseDto> searchKeyword(String query, UserDetailsImpl userDetails) {
        List<EstateResponseDto> estateResponseDtoList = new ArrayList<>();
        System.out.println(query);
        String keyword = "";
        if (query.equals("맛집")) {
            keyword = "양재동";
        } else if (query.equals("역")) {
            keyword = "서초동";
        }
        List<Estate> estates = estateRepository.searchAllBydong(keyword);
        System.out.println(estates.size());
        int i = 0;
        for (Estate estate : estates) {
            boolean mylike = favoriteRepository.existsByEstateidAndUserid(estate.getId(), userDetails.getId());
            EstateResponseDto estateResponseDto = new EstateResponseDto(estate, mylike);
            estateResponseDtoList.add(estateResponseDto);
            i++;
            if (i == 4) break;
        }
        return estateResponseDtoList;
    }

    //리스트 선택후 조회
    public EstateResponseDto showDetail(Long estateid, User user) {
        Estate estate = estateRepository.findById(estateid).orElseThrow(
                () -> new IllegalArgumentException("사라진 매물입니다")
        );
        CoordinateEstate coordinateEstate = coordinateEstateRepository.findByEstateid(estateid);
        CoordinateResponseDto coordinateResponseDto = new CoordinateResponseDto(coordinateEstate);
        boolean mylike = favoriteRepository.existsByEstateidAndUserid(estateid, user.getId());
        return new EstateResponseDto(estate, mylike,coordinateResponseDto);
    }


    public SearchDto searchTowm(String query, UserDetailsImpl userDetails, int pagenum, String monthly,String depositlimit,String feelimit) throws InterruptedException {
        List<EstateResponseDto> estateResponseDtoList = new ArrayList<>();
        String keyword = naverSearchApi.getQuery(query);
        final int start = 10 * pagenum;
        int defaultDepositfee =1000000;
        int defaultFeelimit =1000000;
        String defaultmonthly =null;
        if(!monthly.equals("undefined") & !monthly.equals("null")){
            defaultmonthly=monthly;
        }
        if(!depositlimit.equals("undefined") & !depositlimit.equals("null")){
            defaultDepositfee=Integer.parseInt(depositlimit);
        }
        if(!feelimit.equals("undefined") & !feelimit.equals("null")){
            defaultFeelimit=Integer.parseInt(feelimit);
        }
        List<Estate> estates = estateRepository.searchAllByQuery(query,keyword,start,defaultmonthly,defaultDepositfee,defaultFeelimit);
        int size =estateRepository.countAllByQuery(query,keyword,defaultDepositfee,defaultFeelimit);



//        if (query.contains("시")) {
//            estates = estateRepository.searchAllByCityQuery(query,start);
//            size = estateRepository.countAllByCity(query);
//        } else if (query.contains("구")) {
//            estates = estateRepository.searchAllByGuQuery(query,start);
//            size = estateRepository.countAllByGu(query);
//        } else {
//            estates = estateRepository.searchAllByDongQuery(query,start);
//            size = estateRepository.countAllByDong(query);
//        }
        System.out.println(size);

        int i = 0;
        for (Estate estate : estates) {
            boolean mylike = favoriteRepository.existsByEstateidAndUserid(estate.getId(), userDetails.getId());
            CoordinateEstate coordinateEstate = coordinateEstateRepository.findByEstateid(estate.getId());
            CoordinateResponseDto coordinateResponseDto = new CoordinateResponseDto(coordinateEstate);
            EstateResponseDto estateResponseDto = new EstateResponseDto(estate, query, mylike,coordinateResponseDto);
            estateResponseDtoList.add(estateResponseDto);
            i++;
        }

        int totalpage = 0;
        if (size % 10 == 0) {
            totalpage = size / 10;
        } else {
            totalpage = size/10 + 1;
        }
        return new SearchDto(estateResponseDtoList, totalpage, pagenum + 1,query);
    }

    //핫한 매물 보기
    public List<Map<String, Object>> searchHot(UserDetailsImpl userDetails) {
        //        for(Map<String,Object> asdd : asd){
//            boolean mylike = favoriteRepository.existsByEstateidAndUserid(Long.valueOf(String.valueOf(asdd.get("id"))),userDetails.getId());
//            asdd.put("mylike",mylike);
//        }
        return favoriteRepository.countUseridQuery();
    }

    //구별로 모아보기


    // 찜한것 보기
    public List<EstateResponseDto> showFavorite(UserDetailsImpl userDetails) {

        // 찜한 매물 목록
        List<Favorite> favoriteList = favoriteRepository.findAllByUseridAndType(userDetails.getId(),"사무실");

        List<EstateResponseDto> estateResponseDtos = new ArrayList<>();
        for (int i = 0; i < favoriteList.size(); i++) {
            System.out.println(favoriteList.get(i).getEstateid());
            Estate estate = estateRepository.findById(favoriteList.get(i).getEstateid()).orElseThrow(
                    () -> new NullPointerException("게시글이 없습니다"));
            EstateResponseDto estateResponseDto = new EstateResponseDto(estate, true);
            estateResponseDtos.add(estateResponseDto);

        }
        return estateResponseDtos;
    }


    public MapResponseDto showEstate(double minX, double maxX, double minY, double maxY, int level, UserDetailsImpl userDetails, String depositlimit, String feelimit, String monthly) {
        long temp1 = System.currentTimeMillis();

//        List<String> cities = estateRepository.findCity(minX,maxX,minY,maxY);
        List<String> cities;

        if (level < 7) {
            cities = estateRepository.findDongQuery(minX, maxX, minY, maxY);
        } else if (level == 7 || level == 8) {
            cities = estateRepository.findGuQuery(minX, maxX, minY, maxY);
        } else {
            cities = estateRepository.findCityQuery(minX, maxX, minY, maxY);
        }
        long temp2 = System.currentTimeMillis();
        System.out.println("temp1:");
        System.out.println(temp2 - temp1);
//        System.out.println("size"+cities2.size());
//        Iterator<String> it = cities.iterator();
        List<CityResponseDto> cityResponseDtoList = new ArrayList<>();
        for (int i = 0; i < cities.size(); i++) {
            String title = cities.get(i);

            int estate_cnt = 0;
            float avg = 0f;
            int defaultDepositfee =1000000;
            int defaultFeelimit =1000000;
            String defaultmonthly ="";
            if(!monthly.equals("undefined") & !monthly.equals("null")){
                defaultmonthly=monthly;
            }
            if(!depositlimit.equals("undefined") & !depositlimit.equals("null")){
                defaultDepositfee=Integer.parseInt(depositlimit);
            }
            if(!feelimit.equals("undefined") & !feelimit.equals("null")){
                defaultFeelimit=Integer.parseInt(feelimit);
            }
            if (level < 7) {
                estate_cnt = estateRepository.countDongQuery(title,monthly,defaultDepositfee,defaultFeelimit);
                avg = (float) (estateRepository.dongAvgQuery(title)/estateRepository.dongAreaAvgQuery(title)* 3.3);
            } else if (level == 7 || level == 8) {
                estate_cnt = estateRepository.countGuQuery(title,monthly,defaultDepositfee,defaultFeelimit);
                avg = (float) (estateRepository.guAvgQuery(title)/estateRepository.guAvgAreaQuery(title) *3.3);
            } else {
                estate_cnt = estateRepository.countCityQuery(title,monthly,defaultDepositfee,defaultFeelimit);
                avg = (float) (estateRepository.cityAvgQuery(title)/estateRepository.cityAreaAvgQuery(title) * 3.3);
            }
            avg = Integer.parseInt(String.valueOf(Math.round(avg)));
            String response = convertAddress.convertAddress(title);
            CoordinateResponseDto coordinateResponseDtoDtoDto = convertAddress.fromJSONtoItems(response);

            CityResponseDto cityResponseDto = new CityResponseDto(title, coordinateResponseDtoDtoDto, estate_cnt, (int) avg);
            cityResponseDtoList.add(cityResponseDto);
        }
        long temp3 = System.currentTimeMillis();
        System.out.println("temp2:");
        System.out.println(temp3 - temp2);
        System.out.println("총:");
        System.out.println(temp3 - temp1);
        return new MapResponseDto(level, cityResponseDtoList);
    }
}
