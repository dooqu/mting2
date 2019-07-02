package cn.xylink.mting.utils;

/**
 * 接口签名工具类
 *
 * @author hecj
 */

public class SignKit {


    /**
     * 签名
     *
     * @param
     * @return
     */

    //@SuppressWarnings({"unchecked", "rawtypes"})

//    public static String sign(Object obj) {
//
//        if (obj instanceof Request) {
//            return StringUtil.string2MD5(getDictStr(obj));
//        }
//
//        throw new RuntimeException("暂时不支持此类型生成签名," + obj.getClass());
//
//    }


//    private static String getDictStr(Object obj) {
//        String str = Request.getDesKey();
//        try {
//            Class<?> aClass = obj.getClass();
//            Field[] fields = aClass.getDeclaredFields();
//            Class<?> superclass = aClass.getSuperclass();
//            Field[] superFields = superclass.getDeclaredFields();
//            Class<?> ssClass = superclass.getSuperclass();
//            Field[] ssFields = ssClass.getDeclaredFields();
//            List<String> list = new ArrayList<>();
//            Map<String, Field> fids = new HashMap<>();
//            for (int i = 0; i < fields.length; i++) {
//                String name = fields[i].getName();
//                if (name.contains("$")||"sign".equals(name) || "desKey".equals(name)||"serialVersionUID".equals(name)) {
//                    continue;
//                }
//                list.add(name);
//                fids.put(name, fields[i]);
//            }
//            for (int i = 0; i < superFields.length; i++) {
//                String name = superFields[i].getName();
//                if (name.contains("$")||"sign".equals(name) || "desKey".equals(name)||"serialVersionUID".equals(name)){
//                    continue;
//                }
//                list.add(name);
//                fids.put(name, superFields[i]);
//            }
//            for (int i = 0; i < ssFields.length; i++) {
//                String name = ssFields[i].getName();
//                if (name.contains("$")||"sign".equals(name) || "desKey".equals(name)||"serialVersionUID".equals(name)){
//                    continue;
//                }
//                list.add(name);
//                fids.put(name, ssFields[i]);
//            }
//            Collections.sort(list);
//            for (String key : list) {
//                Object value = null;
//                Field field = fids.get(key);
//                field.setAccessible(true);
//                value = field.get(obj);
//                if (value == null) {
//                    value = "";
//                }
//                str += String.valueOf(value);
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return str;
//
//    }

}