package cn.com.xiaofabo.scia.aiawardcheck.fileprocessor;

public class DocUtil {
	public static String numberToCN(char number) {
        if (number > '9' || number < '0') {
            return null;
        }
        String toReturn;
        switch (number) {
            case '0':
                toReturn = "○";
                break;
            case '1':
                toReturn = "一";
                break;
            case '2':
                toReturn = "二";
                break;
            case '3':
                toReturn = "三";
                break;
            case '4':
                toReturn = "四";
                break;
            case '5':
                toReturn = "五";
                break;
            case '6':
                toReturn = "六";
                break;
            case '7':
                toReturn = "七";
                break;
            case '8':
                toReturn = "八";
                break;
            case '9':
                toReturn = "九";
                break;
            default:
                toReturn = "X";
                break;
        }
        return toReturn;
    }
	
	public static String numberToCN(int number) {
        if (number > 9 || number < 0) {
            return "";
        }
        String toReturn;
        switch (number) {
            case 0:
                toReturn = "○";
                break;
            case 1:
                toReturn = "一";
                break;
            case 2:
                toReturn = "二";
                break;
            case 3:
                toReturn = "三";
                break;
            case 4:
                toReturn = "四";
                break;
            case 5:
                toReturn = "五";
                break;
            case 6:
                toReturn = "六";
                break;
            case 7:
                toReturn = "七";
                break;
            case 8:
                toReturn = "八";
                break;
            case 9:
                toReturn = "九";
                break;
            default:
                toReturn = "X";
                break;
        }
        return toReturn;
    }
}
