from selenium import webdriver

driver = webdriver.Chrome('D:/cwral/chromedriver')
driver.implicitly_wait(3)
driver.get('******')
# 아이디/비밀번호를 입력 후 로그인
driver.find_element_by_name('userid').send_keys('***')
driver.find_element_by_name('userpwd').send_keys('***')
driver.find_element_by_class_name('button').click()

# 메인화면에서 플랫폼 관리를 선택
driver.find_element_by_xpath("//ul[@class='sidebar-menu tree']/li[3]/a").click()
driver.find_element_by_xpath("//ul[@class='sidebar-menu tree']/li[3]/ul/li/a").click()

# 모든 플랫폼 선택 후 패스워드변경 요청 버튼 클릭
driver.find_element_by_name('allCheck').click()
driver.find_element_by_xpath("//div[@class='btn_wrap01']/button[2]").click()

# 패스워드변경 팝업으로 활성탭 변경 후 변경 요청 클릭
handles = driver.window_handles;
driver.switch_to.window(handles[-1]); # -1 변경된 활성 탭 0 부모 탭 (확실하진 않음!!!)
driver.find_element_by_xpath("//div[@class='btn_wrap']/button[1]").click()

# 확인 다이얼로그로 활성탭 변경 후 확인 선택
dialog = driver.window_handles;
driver.switch_to.window(dialog[-1])
driver.find_element_by_xpath("/html/body/div[2]/div[3]/div[1]/button[1]").click()
driver.find_element_by_xpath("/html/body/div[3]/div[3]/div[1]/button[1]").click()

driver.switch_to.window(handles[0]);

#driver.close()









