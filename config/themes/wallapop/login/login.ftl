<#import "template.ftl" as layout>
  <@layout.registrationLayout displayMessage=!messagesPerField.existsError('username','password') displayInfo=realm.password && realm.registrationAllowed && !registrationDisabled??; section>
    <#if section="header">
      ${msg("loginAccountTitle")}
      <#elseif section="form">
        <div id="kc-form">
          <#if messagesPerField.existsError('username','password')>
            <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
              ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
            </span>
          </#if>
          <div id="kc-form-wrapper">
            <#if realm.password>
              <form id="kc-form-login" onsubmit="login.disabled = true; return true;" action="${url.loginAction}" method="post">
                <#if !usernameHidden??>
                  <div class="${properties.kcFormGroupClass!} wallapop-input__wrapper">
                    <label for="username" class="${properties.kcLabelClass!} wallapop-input__label">
                      <#if !realm.loginWithEmailAllowed>
                        ${msg("username")}
                        <#elseif !realm.registrationEmailAsUsername>
                          ${msg("usernameOrEmail")}
                          <#else>
                            ${msg("email")}
                      </#if>
                    </label>
                    <input tabindex="2" id="username" class="${properties.kcInputClass!} wallapop-input__element" name="username" value="${(login.username!'')}" type="text" autocomplete="username"
                      dir="ltr" onfocus="handleInputFocusBlur(this)" onblur="handleInputFocusBlur(this)" oninput="handleInputChange(this)" required />
                  </div>
                </#if>
                <div class="${properties.kcFormGroupClass!} wallapop-input__wrapper">
                  <label for="password" class="${properties.kcLabelClass!} wallapop-input__label">
                    ${msg("password")}
                  </label>
                  <div class="${properties.kcInputGroup!}" dir="ltr">
                    <input tabindex="3" id="password" class="${properties.kcInputClass!} wallapop-input__element" name="password" type="password" autocomplete="current-password"
                      onfocus="handleInputFocusBlur(this)" onblur="handleInputFocusBlur(this)" oninput="handleInputChange(this)" required />
                    <div class="wallapop-input__icon-section">
                      <button class="${properties.kcFormPasswordVisibilityButtonClass!} wallapop-input__icon" type="button" aria-label="${msg("showPassword")}"
                        aria-controls="password" data-password-toggle tabindex="4"
                        data-icon-show="${properties.kcFormPasswordVisibilityIconHide!}" data-icon-hide="${properties.kcFormPasswordVisibilityIconShow!}"
                        data-label-show="${msg('showPassword')}" data-label-hide="${msg('hidePassword')}">
                        <i class="${properties.kcFormPasswordVisibilityIconHide!}" aria-hidden="true"></i>
                      </button>
                    </div>
                  </div>
                  <#if usernameHidden?? && messagesPerField.existsError('username','password')>
                    <span id="input-error" class="${properties.kcInputErrorMessageClass!}" aria-live="polite">
                      ${kcSanitize(messagesPerField.getFirstError('username','password'))?no_esc}
                    </span>
                  </#if>
                </div>
                <div class="${properties.kcFormGroupClass!} ${properties.kcFormSettingClass!}">
                  <div id="kc-form-options">
                    <#if realm.rememberMe && !usernameHidden??>
                      <div class="checkbox">
                        <label>
                          <#if login.rememberMe??>
                            <input tabindex="5" id="rememberMe" name="rememberMe" type="checkbox" checked>
                            ${msg("rememberMe")}
                            <#else>
                              <input tabindex="5" id="rememberMe" name="rememberMe" type="checkbox">
                              ${msg("rememberMe")}
                          </#if>
                        </label>
                      </div>
                    </#if>
                  </div>
                  <div class="${properties.kcFormOptionsWrapperClass!}">
                    <#if realm.resetPasswordAllowed>
                      <span><a tabindex="6" href="${url.loginResetCredentialsUrl}">
                          ${msg("doForgotPassword")}
                        </a></span>
                    </#if>
                  </div>
                </div>
                <span class="wallapop-terms__container">
                  ${msg("legalConsentTextPart1")}
                  <a
                    class="wallapop-terms__link"
                    href="${msg('termsAndConditionsUrl')}"
                    rel="noopener noreferrer"
                    target="_blank">
                    ${msg("legalConsentTextPart2")}
                  </a>
                  ${msg("legalConsentTextPart3")}
                  <a
                    class="wallapop-terms__link"
                    href="${msg('privacyPolicyUrl')}"
                    rel="noopener noreferrer"
                    target="_blank">
                    ${msg("legalConsentTextPart4")}
                  </a>
                  ${msg("legalConsentTextPart5")}
                </span>
                <div id="kc-form-buttons" class="${properties.kcFormGroupClass!}">
                  <input type="hidden" id="id-hidden-input" name="credentialId" <#if auth.selectedCredential?has_content>value="${auth.selectedCredential}"
            </#if>/>
            <input tabindex="7" class="${properties.kcButtonClass!} ${properties.kcButtonPrimaryClass!} ${properties.kcButtonBlockClass!} ${properties.kcButtonLargeClass!} wallapop-button__button" name="login" id="kc-login" type="submit" value="${msg("doLogIn")}" />
          </div>
          </form>
    </#if>
    </div>
    </div>
    <script type="module" src="${url.resourcesPath}/js/passwordVisibility.js"></script>
    <#elseif section="info">
      <#if realm.password && realm.registrationAllowed && !registrationDisabled??>
        <div id="kc-registration-container">
          <div id="kc-registration">
            <span>
              ${msg("noAccount")}
              <a tabindex="8"
                href="${url.registrationUrl}">
                ${msg("doRegister")}
              </a></span>
          </div>
        </div>
      </#if>
      <#elseif section="socialProviders">
        <#if realm.password && social?? && social.providers?has_content>
          <div id="kc-social-providers" class="${properties.kcFormSocialAccountSectionClass!}">
            <hr />
            <h2>
              ${msg("identity-provider-login-label")}
            </h2>
            <ul class="${properties.kcFormSocialAccountListClass!}
<#if social.providers?size gt 3>
${properties.kcFormSocialAccountListGridClass!}
</#if>">
              <#list social.providers as p>
                <li>
                  <a id="social-${p.alias}" class="${properties.kcFormSocialAccountListButtonClass!}
<#if social.providers?size gt 3>
${properties.kcFormSocialAccountGridItem!}
</#if>"
                    type="button" href="${p.loginUrl}">
                    <#if p.iconClasses?has_content>
                      <i class="${properties.kcCommonLogoIdP!} ${p.iconClasses!}" aria-hidden="true"></i>
                      <span class="${properties.kcFormSocialAccountNameClass!} kc-social-icon-text">
                        ${p.displayName!}
                      </span>
                      <#else>
                        <span class="${properties.kcFormSocialAccountNameClass!}">
                          ${p.displayName!}
                        </span>
                    </#if>
                  </a>
                </li>
              </#list>
            </ul>
          </div>
        </#if>
        </#if>
  </@layout.registrationLayout>