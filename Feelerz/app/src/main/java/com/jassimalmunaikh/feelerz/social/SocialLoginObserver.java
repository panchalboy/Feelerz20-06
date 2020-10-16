package com.jassimalmunaikh.feelerz.social;

import java.util.Map;

public interface SocialLoginObserver
{
    void OnLoginSuccess(Map<String, String> request);
    void OnLoginFailure();
}
