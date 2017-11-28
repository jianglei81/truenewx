package org.truenewx.web.version;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.truenewx.core.Strings;
import org.truenewx.core.version.VersionReader;
import org.truenewx.web.rpc.server.annotation.RpcController;
import org.truenewx.web.rpc.server.annotation.RpcMethod;
import org.truenewx.web.security.annotation.Accessibility;

/**
 * 版本显示控制器
 *
 * @author jianglei
 * @since JDK 1.8
 */
@RpcController
@RequestMapping("/version")
public class VersionController {

    @Autowired(required = false)
    private VersionReader versionReader;

    @RequestMapping("/build")
    @ResponseBody
    public String build() {
        if (this.versionReader != null) {
            return this.versionReader.getVersion(true);
        }
        return Strings.EMPTY;
    }

    @RequestMapping("/release")
    @ResponseBody
    public String release() {
        if (this.versionReader != null) {
            return this.versionReader.getVersion(false);
        }
        return Strings.EMPTY;
    }

    @RpcMethod
    @Accessibility(anonymous = true)
    public String getVersion(final boolean withBuild) {
        if (this.versionReader != null) {
            return this.versionReader.getVersion(withBuild);
        }
        return null;
    }

    @RpcMethod
    @Accessibility(anonymous = true)
    public String getBuild() {
        if (this.versionReader != null) {
            return this.versionReader.getBuild();
        }
        return null;
    }

}
