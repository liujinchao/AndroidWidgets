package com.aihuishou.business.ocr.entity

/**
 * @ClassName  AccessTokenBean
 * @author liujc
 * @date 2019/11/22
 * @Description (这里用一句话描述这个类的作用)
 */
data class AccessTokenBean(var refresh_token: String? = null,
                           var expires_in: Int = 0,
                           var scope: String? = null,
                           var session_key: String? = null,
                           var access_token: String? = null,
                           var session_secret: String? = null)