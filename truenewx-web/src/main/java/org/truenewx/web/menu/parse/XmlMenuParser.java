package org.truenewx.web.menu.parse;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.LoggerFactory;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.truenewx.core.Strings;
import org.truenewx.core.functor.algorithm.impl.AlgoParseString;
import org.truenewx.core.functor.impl.FuncFindClass;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.http.HttpResource;
import org.truenewx.web.menu.model.ActableMenuItem;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.menu.model.MenuItemAction;
import org.truenewx.web.menu.util.MenuUtil;
import org.truenewx.web.rpc.RpcPort;
import org.truenewx.web.security.authority.Authority;

/**
 * 获取XML菜单文件
 *
 * @author jianglei
 * @since JDK 1.8
 */
public class XmlMenuParser implements MenuParser, ResourceLoaderAware {

    private FuncFindClass funcFindClass;

    @Override
    public void setResourceLoader(ResourceLoader resourceLoader) {
        this.funcFindClass = new FuncFindClass(resourceLoader.getClassLoader(), true);
    }

    @Override
    public Menu parser(InputStream inputStream) {
        try {
            SAXReader reader = new SAXReader();
            Document doc = reader.read(inputStream);
            Element menuElement = doc.getRootElement();
            Menu menu = new Menu(menuElement.attributeValue("name"));
            menu.getAnonymousResources().addAll(getResources(menuElement.element("anonymous")));
            menu.getLoginedResources().addAll(getResources(menuElement.element("logined")));
            menu.getItems().addAll(getItems(menuElement, null));
            return menu;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage(), e);
        }
        return null;
    }

    private List<HttpResource> getResources(Element element) {
        List<HttpResource> resources = new ArrayList<>();
        if (element != null) {
            for (Object obj : element.elements()) {
                Element resElement = (Element) obj;
                String method = resElement.attributeValue("method");
                if ("link".equals(resElement.getName())) {
                    String href = resElement.attributeValue("href");
                    HttpLink link;
                    if (StringUtils.isNotBlank(method)) {
                        link = new HttpLink(href, EnumUtils.getEnum(HttpMethod.class, method));
                    } else {
                        link = new HttpLink(href);
                    }
                    resources.add(link);
                } else if ("rpc".equals(resElement.getName())) {
                    String beanId = resElement.attributeValue("id");
                    RpcPort rpcPort = new RpcPort(beanId, method);
                    resources.add(rpcPort);
                }
            }
        }
        return resources;
    }

    /**
     * 获取菜单项
     *
     * @param element
     *            元素
     * @param parentOptions
     * @return items 菜单项集合
     *
     * @author jianglei
     */
    private List<MenuItem> getItems(Element element, Map<String, Object> parentOptions) {
        List<MenuItem> items = new ArrayList<>();
        for (Object itemObj : element.elements("item")) {
            Element itemElement = (Element) itemObj;
            String type = itemElement.attributeValue("type");
            if (StringUtils.isBlank(type) || ActableMenuItem.TYPE.equals(type)) { // 默认或指定为动作型菜单项
                String caption = itemElement.attributeValue("caption");
                String icon = itemElement.attributeValue("icon");
                MenuItemAction action = getAction(itemElement.element("action"));
                ActableMenuItem item = new ActableMenuItem(caption, icon, action);
                item.getProfiles().addAll(getProfiles(itemElement));
                item.getOptions().putAll(getOptions(itemElement, parentOptions));
                item.getCaptions().putAll(getCaptions(itemElement));
                item.getSubs().addAll(getItems(itemElement, item.getOptions()));
                items.add(item);
            } else { // 非动作型菜单项
                MenuItem item = new MenuItem(type);
                item.getProfiles().addAll(getProfiles(itemElement));
                item.getOptions().putAll(getOptions(itemElement, parentOptions));
                items.add(item);
            }
        }
        return items;
    }

    private MenuItemAction getAction(Element element) {
        if (element != null) {
            Authority authority = getAuthority(element);
            String href = element.attributeValue("href");
            MenuItemAction action = new MenuItemAction(authority, new HttpLink(href));
            action.getResources().addAll(getResources(element));
            return action;
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Authority getAuthority(Element element) {
        String permission = element.attributeValue("permission");
        String className = element.attributeValue("type");
        if (StringUtils.isNotBlank(className)) {
            Class<?> type = this.funcFindClass.apply(className);
            if (type != null) {
                if (type.isEnum()) { // 如果为枚举类型，则取枚举的名称
                    Enum<?> enumConstant = Enum.valueOf((Class<Enum>) type, permission);
                    permission = MenuUtil.getPermission(enumConstant);
                } else { // 否则取该类型的静态字符串属性值
                    Object value = ClassUtil.getPublicStaticPropertyValue(type, permission);
                    if (value != null && value instanceof String) {
                        permission = (String) value;
                    }
                }
            }
        }
        String role = element.attributeValue("role");
        return new Authority(role, permission);
    }

    /**
     * 获取菜单说明
     *
     * @param element
     *            元素
     * @return captions 获取菜单说明集合
     *
     * @author jianglei
     */
    private Map<Locale, String> getCaptions(Element element) {
        Map<Locale, String> captions = new HashMap<>();
        for (Object captionObj : element.elements("caption")) {
            Element captionElement = (Element) captionObj;
            captions.put(new Locale(captionElement.attributeValue("locale")),
                    captionElement.getTextTrim());
        }
        return captions;
    }

    /**
     * 获取可见环境集合
     *
     * @param element
     *            元素
     * @return 可见环境集合
     *
     * @author jianglei
     */
    private Set<String> getProfiles(Element element) {
        Set<String> links = new LinkedHashSet<>();
        String profile = element.attributeValue("profile");
        if (StringUtils.isNotBlank(profile)) {
            String[] profiles = profile.split(Strings.COMMA);
            CollectionUtil.addAll(links, profiles);
        }
        return links;
    }

    /**
     * 获取Options
     *
     * @param element
     *            元素
     * @param parentOptions
     *
     * @return options
     *
     * @author jianglei
     */
    private Map<String, Object> getOptions(Element element, Map<String, Object> parentOptions) {
        Map<String, Object> options = new HashMap<>();
        Element optionsElement = element.element("options");
        // 需要继承父级选项集时，先继承父级选项集
        if (requiresInheritOptions(optionsElement, parentOptions)) {
            options.putAll(parentOptions);
        }
        // 设置了选项集则再加入自身选项集
        if (optionsElement != null) {
            options.putAll(getOption(optionsElement));
        }
        return options;
    }

    private boolean requiresInheritOptions(Element optionsElement,
            Map<String, Object> parentOptions) {
        if (parentOptions == null || parentOptions.isEmpty()) { // 如果父级选项集为空，则不需要继承
            return false;
        }
        if (optionsElement == null) { // 未设置选项集则默认需要继承
            return true;
        }
        // 设置有选项集，则根据inherit属性判断，inherit未设置时默认视为false
        return Boolean.valueOf(optionsElement.attributeValue("inherit"));
    }

    /**
     * 获取Option
     *
     * @param element
     *
     * @return options
     * @author jianglei
     */
    private Map<String, Object> getOption(Element element) {
        Map<String, Object> options = new HashMap<>();
        for (Object optionObj : element.elements("option")) {
            Element optionElement = (Element) optionObj;
            String className = optionElement.attributeValue("type");
            String value = optionElement.getTextTrim();
            if (StringUtils.isNotBlank(className)) {
                Class<?> type = this.funcFindClass.apply(className);
                Object typeValue = AlgoParseString.visit(value, type);
                options.put(optionElement.attributeValue("name"), typeValue);
            } else {
                options.put(optionElement.attributeValue("name"), value);
            }
        }
        return options;
    }
}
