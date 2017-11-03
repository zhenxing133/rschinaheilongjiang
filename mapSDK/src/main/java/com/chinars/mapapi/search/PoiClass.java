package com.chinars.mapapi.search;

/**
 * Poi分类
 * @author Administrator
 *
 */
public class PoiClass {

	/**餐饮**/
	public static final int TYPE_EATTING=0x1080;
	public static final int TYPE_EATTING_MCDONALD=0x10C0;
	public static final int TYPE_EATTING_KFC=0x10C1;
	public static final int TYPE_EATTING_PIZZA=0x10C2;
	public static final int TYPE_EATTING_DICOS=0x10C3;
	public static final int TYPE_EATTING_DOMINOS=0x10C7;
	public static final int TYPE_EATTING_CHINA=0x1380;
	public static final int TYPE_EATTING_TEA=0x1600;
	
	/**零售业 **/
	public static final int TYPE_RETAIL=0x2000;
	
	/**便利店**/
	public static final int TYPE_RETAIL_CVS=0x2080;
	
	/**超市**/
	public static final int TYPE_RETAIL_SUPERMARKET=0x2100;
	public static final int TYPE_RETAIL_MAIL=0x2300;
	public static final int TYPE_RETAIL_BOOK=0x2B80;
	public static final int TYPE_RETAIL_MEDICINE=0x2800;
	public static final int TYPE_RETAIL_COMMUNICATIONS=0x2F80;
	public static final int TYPE_RETAIL_NEWSSTAND=0x2D00;
	
	
	/**汽车 **/
	public static final int TYPE_CAR=0x4000;
	
	public static final int TYPE_CAR_GAS_STATION=0x4080;
	public static final int TYPE_CAR_PARK=0x4100;
	public static final int TYPE_CAR_SERVICE=0x4300;
	
	/**住宿 **/
	public static final int TYPE_ACCOMMODATION=0x5080;
	
	public static final int TYPE_ACCOMMODATION_NO_STAR=0x5082;
	public static final int TYPE_ACCOMMODATION_THREE_STAR=0x5083;
	public static final int TYPE_ACCOMMODATION_FOUR_STAR=0x5084;
	public static final int TYPE_ACCOMMODATION_FIVE_STAR=0x5085;
	public static final int TYPE_ACCOMMODATION_GUESTHOUSE=0x5380;
	
	/**休闲  relaxation **/
    public static final int TYPE_RELAXATION=0x6080;
	
	public static final int TYPE_RELAXATION_MOVIE=0x6500;
	public static final int TYPE_RELAXATION_KTV=0x6680;
	public static final int TYPE_RELAXATION_CLUB=0x6C00;
	
	/**公共设施 **/
	public static final int TYPE_COMMUNITY=0x7080;

	public static final int TYPE_COMMUNITY_GOVERNMENT=0x7080;
	public static final int TYPE_COMMUNITY_WIFI=0x7A00;
	public static final int TYPE_COMMUNITY_POLICY=0x7100;
	public static final int TYPE_COMMUNITY_HOSPITAL=0x7200;
	public static final int TYPE_COMMUNITY_TOILET=0x7880;

	/**交通**/
	public static final int TYPE_TRANSPORTATION =0x8080;

	public static final int TYPE_TRANSPORTATION_STATION =0x8080;
	public static final int TYPE_TRANSPORTATION_RAILWAY_STATION=0x8081;
	public static final int TYPE_TRANSPORTATION_METRO_STATION=0x8082;
	public static final int TYPE_TRANSPORTATION_LONGWAY_BUS_STATION=0x808D;
	public static final int TYPE_TRANSPORTATION_BUS_STATION=0x808B;
	public static final int TYPE_TRANSPORTATION_TAXI=0x808C;
	public static final int TYPE_TRANSPORTATION_BICYCLE=0x80B0;
	public static final int TYPE_TRANSPORTATION_AIRPLANE=0x8100;
	public static final int TYPE_TRANSPORTATION_HIGHWAY_EXIT=0x8301;
	public static final int TYPE_TRANSPORTATION_HIGHWAY_ENTRER=0x8302;
	public static final int TYPE_TRANSPORTATION_HIGHWAY_SERVICE=0x8380;
	public static final int TYPE_TRANSPORTATION_HIGHWAY_CHARGE=0x8401;
	
	/**文化、媒体 **/
	public static final int TYPE_CULTURE =0x9080;

	public static final int TYPE_CULTURE_SCENIC_SPOT=0x9080;
	public static final int TYPE_CULTURE_CHURCH=0x9200;
	public static final int TYPE_CULTURE_MUSEUM=0x9300;
	
	/**银行 **/
	public static final int TYPE_BANK=0xA180;
	
	/**保险 **/
	public static final int TYPE_INSURANCE=0xAA80;
	
	/**教育 **/
	public static final int TYPE_EDUCATION=0xA700;
	
	/**小区**/
	public static final int TYPE_HOUSING_ESTATE=0xA900;
	
	/**公司企业 **/
	public static final int TYPE_COMPANY=0xA980;
	
	/**自然地物**/
	public static final int TYPE_NATURE =0xB000;
	
	public static String getPoiKindName(int kindCode){
		int type=kindCode&0xF000;
		switch(type){
		case 0x1000:
			switch(kindCode){
			case TYPE_EATTING_CHINA:
				return "中餐馆";
			case TYPE_EATTING_DICOS:
				return "德克士";
			case TYPE_EATTING_DOMINOS:
				return "达美乐";
			case TYPE_EATTING_KFC:
				return "肯德基";
			case TYPE_EATTING_MCDONALD:
				return "麦当劳";
			case TYPE_EATTING_PIZZA:
				return "必胜客";
				default:
					if((kindCode&TYPE_EATTING_TEA)==TYPE_EATTING_TEA){
						return "茶楼，咖啡店";
					}else{
						return "餐饮";
					}
			}
		case 0x2000:
			switch(kindCode){
			case TYPE_RETAIL:
				return "零售";
			case TYPE_RETAIL_BOOK:
				return "书店";
			case TYPE_RETAIL_COMMUNICATIONS:
				return "通讯业";
			case TYPE_RETAIL_CVS:
				return "便利店";
			case TYPE_RETAIL_MAIL:
				return "商场";
			case TYPE_RETAIL_MEDICINE:
				return "药店";
			case TYPE_RETAIL_NEWSSTAND:
				return "报刊亭";
			case TYPE_RETAIL_SUPERMARKET:
				return "超市";
				default:
					if((kindCode&TYPE_RETAIL_CVS)==TYPE_RETAIL_CVS){
						return "便利店";
					}else if((kindCode&TYPE_RETAIL_SUPERMARKET)==TYPE_RETAIL_SUPERMARKET){
						return "超市";
					}else if((kindCode&TYPE_RETAIL_BOOK)==TYPE_RETAIL_BOOK){
						return "书店";
					}else if((kindCode&TYPE_RETAIL_COMMUNICATIONS)==TYPE_RETAIL_COMMUNICATIONS){
						return "通讯业";
					}else{
						return "零售";
					}
			}
		case 0x4000:

			if((kindCode&TYPE_CAR_GAS_STATION)==TYPE_CAR_GAS_STATION){
				return "加油站";

			}else if((kindCode&TYPE_CAR_PARK)==TYPE_CAR_PARK){
				return "停车场";
			}else if((kindCode&TYPE_CAR_SERVICE)==TYPE_CAR_SERVICE){
				 return "汽车服务点";
			}else{
				return "汽车";
			}
		case 0x5000:
			switch(kindCode){
			case TYPE_ACCOMMODATION:
				return "住宿";
			case TYPE_ACCOMMODATION_FIVE_STAR:
				return "5星级宾馆";
			case TYPE_ACCOMMODATION_FOUR_STAR:
				return "4星级宾馆";
			case TYPE_ACCOMMODATION_GUESTHOUSE:
				return "旅馆、招待所";
			case TYPE_ACCOMMODATION_NO_STAR:
				return "1，2星级宾馆";
			case TYPE_ACCOMMODATION_THREE_STAR:
				return "3星级宾馆";
				default:
					return "住宿";
			}
		case 0x6000:
			switch(kindCode){
			case TYPE_RELAXATION_CLUB:
				return "夜总会、歌舞 厅、迪厅";
			case TYPE_RELAXATION_KTV:
				return "KTV";
			case TYPE_RELAXATION_MOVIE:
				return "电影院";
			default:
				return "休闲";
			}
		case 0x7000:
			switch(kindCode){
			case TYPE_COMMUNITY_GOVERNMENT:
				return "政府机关";
			case TYPE_COMMUNITY_HOSPITAL:
				return "医院";
			case TYPE_COMMUNITY_WIFI:
				return "WIFI热点";
			case TYPE_COMMUNITY_POLICY:
				return "公安";
			case TYPE_COMMUNITY_TOILET:
				return "";
				default:
					if((kindCode&TYPE_COMMUNITY_GOVERNMENT)==TYPE_COMMUNITY_GOVERNMENT){
						return "政府机关";
					}else if((kindCode&TYPE_COMMUNITY_POLICY)==TYPE_COMMUNITY_POLICY){
						return "公安";
					}else if((kindCode&TYPE_COMMUNITY_HOSPITAL)==TYPE_COMMUNITY_HOSPITAL){
						return "医院";
					}else {
						return "公共设施";
					}
				
			}
		case 0x8000:
			switch(kindCode){
			case TYPE_TRANSPORTATION:
				return "交通";
			case TYPE_TRANSPORTATION_AIRPLANE:
				return "机场";
			case TYPE_TRANSPORTATION_BUS_STATION:
				return "公交车站";
			case TYPE_TRANSPORTATION_BICYCLE:
				return "公共自行车";
			case TYPE_TRANSPORTATION_HIGHWAY_CHARGE:
				return "收费站";
			case TYPE_TRANSPORTATION_HIGHWAY_ENTRER:
				return "高速入口";
			case TYPE_TRANSPORTATION_HIGHWAY_EXIT:
				return "高速出口";
			case TYPE_TRANSPORTATION_HIGHWAY_SERVICE:
				return "高速服务区";
			case TYPE_TRANSPORTATION_LONGWAY_BUS_STATION:
				return "长途客运站";
			case TYPE_TRANSPORTATION_METRO_STATION:
				return "地铁站";
			case TYPE_TRANSPORTATION_RAILWAY_STATION:
				return "火车站";
			case TYPE_TRANSPORTATION_TAXI:
				return "出租车站";
				default:
					return "车站";
					
			}
		case 0x9000:
			switch(kindCode){
			case TYPE_CULTURE_CHURCH:
				return "教堂";
			case TYPE_CULTURE_MUSEUM:
				return "博物馆";
			case TYPE_CULTURE_SCENIC_SPOT:
				return "风景名胜";
			default:
				if((kindCode&TYPE_CULTURE_CHURCH)==TYPE_CULTURE_CHURCH){
					return "教堂";
				}else if((kindCode&TYPE_CULTURE_MUSEUM)==TYPE_CULTURE_MUSEUM){
					return "博物馆";
				}else if((kindCode&TYPE_CULTURE_SCENIC_SPOT)==TYPE_CULTURE_SCENIC_SPOT){
					return "风景名胜";
				}else {
					return "文化";
				}
			}
		case 0xA000:
			if((kindCode&TYPE_BANK)==TYPE_BANK){
				return "银行";
			}else if((kindCode&TYPE_INSURANCE)==TYPE_INSURANCE){
				return "保险";
			}else if((kindCode&TYPE_EDUCATION)==TYPE_EDUCATION){
				return "教育";
			}else if((kindCode&TYPE_HOUSING_ESTATE)==TYPE_HOUSING_ESTATE){
				return "房产";
			}else if((kindCode&TYPE_COMPANY)==TYPE_COMPANY){
				return "企业公司";
			}else {
				return "其他";
			}
		case 0xB000:
			return "自然地物";
		}
			
		return null;
	}
	
	/**
	 * 获得类别名称
	 * @param kindCode 类别代码，4位16进制数
	 * @return
	 */
	public static String getPoiKindName(String kindCode){
		int code=Integer.valueOf(kindCode, 16);
		return getPoiKindName(code);
	}
	
	/**
	 * 获取类别代码所在大类
	 * @param kindCode 类别代码
	 * @return
	 */
	public static int getPoiClass(String kindCode){
		int type=Integer.valueOf(kindCode, 16)&0xF000;
		switch(type){
		case 0x1000:
			return 1;
		case 0x2000:
			return 2;
		case 0x4000:
			return 4;
		case 0x5000:
			return 5;
		case 0x6000:
			return 6;
		case 0x7000:
			return 7;
		case 0x8000:
			return 8;
		case 0x9000:
			return 9;
		case 0xA000:
			return 10;
		case 0xB000:
			return 11;
		}
		return 0;
	}
}
