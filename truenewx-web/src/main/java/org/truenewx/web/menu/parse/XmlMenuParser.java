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
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpMethod;
import org.truenewx.core.Strings;
import org.truenewx.core.functor.algorithm.impl.AlgoParseString;
import org.truenewx.core.functor.impl.FuncFindClass;
import org.truenewx.core.util.ClassUtil;
import org.truenewx.core.util.CollectionUtil;
import org.truenewx.web.http.HttpLink;
import org.truenewx.web.menu.model.Menu;
import org.truenewx.web.menu.model.MenuItem;
import org.truenewx.web.menu.model.MenuOperation;
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
    public void setResourceLoader(final ResourceLoader resourceLoader) {
        this.funcFindClass = new FuncFindClass(resourceLoader.getClassLoader(), true);
    }

    @Override
    public Menu parser(final InputStream inputStream) {
        try {
            final SAXReader reader = new SAXReader();
            final Document doc = reader.read(inputStream);
            final Element menuElement = doc.getRootElement();
            final Menu menu = new Menu(menuElement.attributeValue("name"));
            final List<MenuItem> item = getItems(menuElement, null);
            for (final MenuItem menuItem : item) {
                menu.addItem(menuItem);
            }
            final List<MenuOperation> operations = getOperations(menuElement, null);
            for (final MenuOperation operation : operations) {
                menu.addOperation(operation);
            }
            return menu;
        } catch (final Exception e) {
            e.printStackTrace();
        }
        return null;
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
    private List<MenuItem> getItems(final Element element,
            final Map<String, Object> parentOptions) {
        final List<MenuItem> items = new ArrayList<>();
        for (final Object itemObj : element.elements("item")) {
            final Element itemElement = (Element) itemObj;
            final Authority auth = getAuth(itemElement);
            final String caption = itemElement.attributeValue("caption");
            final String href = itemElement.attributeValue("href");
            final String target = itemElement.attributeValue("target");
            final String icon = itemElement.attributeValue("icon");
            final MenuItem menuItem = new MenuItem(auth, caption, href, target, icon);
            menuItem.getLinks().addAll(getLinks(itemElement));
            menuItem.getProfiles().addAll(getProfiles(itemElement));
            menuItem.getOptions().putAll(getOptions(itemElement, parentOptions));
            menuItem.getCaptions().putAll(getCaptions(itemElement));
            menuItem.getOperations().addAll(getOperations(itemElement, menuItem.getOptions()));
            menuItem.getSubs().addAll(getItems(itemElement, menuItem.getOptions()));
            items.add(menuItem);
        }
        return items;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private Authority getAuth(final Element element) {
        String permission = element.attributeValue("permission");
        final String className = element.attributeValue("type");
        if (StringUtils.isNotBlank(className)) {
            final Class<?> type = this.funcFindClass.apply(className);
            if (type != null) {
                if (type.isEnum()) { // 如果为枚举类型，则取枚举的名称
                    final Enum<?> enumConstant = Enum.valueOf((Class<Enum>) type, permission);
                    permission = MenuUtil.getPermission(enumConstant);
                } else { // 否则取该类型的静态字符串属性值
                    final Object value = ClassUtil.getPublicStaticPropertyValue(type, permission);
                    if (value != null && value instanceof String) {
                        permission = (String) value;
                    }
                }
            }
        }
        final String role = element.attributeValue("role");
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
    private Map<Locale, String> getCaptions(final Element element) {
        final Map<Locale, String> captions = new HashMap<>();
        for (final Object captionObj : element.elements("caption")) {
            final Element captionElement = (Element) captionObj;
            captions.put(new Locale(captionElement.attributeValue("locale")),
                    captionElement.getTextTrim());
        }
        return captions;
    }

    /**
     * 获取菜单操作集合
     *
     * @param element
     *            元素
     * @param parentOptions
     * @return operations 菜单功能项集合
     *
     * @author jianglei
     */
    private List<MenuOperation> getOperations(final Element element,
            final Map<String, Object> parentOptions) {
        final List<MenuOperation> operations = new ArrayList<>();
        for (final Object operationObj : element.elements("operation")) {
            final Element operationElement = (Element) operationObj;
            final Authority auth = getAuth(operationElement);
            final String caption = operationElement.attributeValue("caption");
            final MenuOperation operation = new MenuOperation(auth, caption);
            operation.getCaptions().putAll(getCaptions(operationElement));
            operation.getOptions().putAll(getOptions(operationElement, parentOptions));
            operation.getLinks().addAll(getLinks(operationElement));
            operation.getProfiles().addAll(getProfiles(operationElement));
            operation.getRpcs().addAll(getRpcs(operationElement));
            operations.add(operation);
        }
        return operations;
    }

    /**
     * 获取链接集合
     *
     * @param element
     *            元素
     * @return 链接集合
     *
     * @author jianglei
     */
    private List<HttpLink> getLinks(final Element element) {
        final List<HttpLink> links = new ArrayList<>();
        for (final Object linkObj : element.elements("link")) {
            final Element linkElement = (Element) linkObj;
            final String method = linkElement.attributeValue("method");
            if (StringUtils.isNotBlank(method)) {
                links.add(new HttpLink(linkElement.attributeValue("href"),
                        EnumUtils.getEnum(HttpMethod.class, method)));
            } else {
                links.add(new HttpLink(linkElement.attributeValue("href")));
            }
        }
        return links;
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
    private Set<String> getProfiles(final Element element) {
        final Set<String> links = new LinkedHashSet<>();
        final String profile = element.attributeValue("profile");
        if (StringUtils.isNotBlank(profile)) {
            final String[] profiles = profile.split(Strings.COMMA);
            CollectionUtil.addAll(links, profiles);
        }
        return links;
    }

    /**
     * 获取rpc集合
     *
     * @param element
     *            元素
     * @return rpc集合
     *
     * @author jianglei
     */
    private List<RpcPort> getRpcs(final Element element) {
        final List<RpcPort> rpcs = new ArrayList<>();
        for (final Object rpcObj : element.elements("rpc")) {
            final Element rpcElement = (Element) rpcObj;
            rpcs.add(new RpcPort(rpcElement.attributeValue("id"),
                    rpcElement.attributeValue("method")));
        }
        return rpcs;
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
    private Map<String, Object> getOptions(final Element element,
            final Map<String, Object> parentOptions) {
        final Map<String, Object> options = new HashMap<>();
        final Element optionsElement = element.element("options");
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

    private boolean requiresInheritOptions(final Element optionsElement,
            final Map<String, Object> parentOptions) {
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
    private Map<String, Object> getOption(final Element element) {
        final Map<String, Object> options = new HashMap<>();
        for (final Object optionObj : element.elements("option")) {
            final Element optionElement = (Element) optionObj;
            final String className = optionElement.attributeValue("type");
            final String value = optionElement.getTextTrim();
            if (StringUtils.isNotBlank(className)) {
                final Class<?> type = this.funcFindClass.apply(className);
                final Object typeValue = AlgoParseString.visit(value, type);
                options.put(optionElement.attributeValue("name"), typeValue);
            } else {
                options.put(optionElement.attributeValue("name"), value);
            }
        }
        return options;
    }
}
