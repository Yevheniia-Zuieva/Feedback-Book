<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Нова книга в каталозі</title>
</head>
<body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; background-color: #f4f4f4; padding: 20px;">
    <div style="max-width: 600px; margin: 0 auto; background: #ffffff; border-radius: 8px; padding: 25px; border: 1px solid #e0e0e0;">
        
        <!-- Логотип застосунку з GitHub -->
        <div style="text-align: center;">
            <img src="https://raw.githubusercontent.com/VladPiatachenko/MPF_Labs/lab7-freemaker/web/src/main/resources/static/img/logo.png"
                 style="width:150px; margin-bottom:20px;" alt="BookApp">
        </div>

        <!-- Стилізована панель повідомлення -->
        <div style="background-color: #e8f5e9; border-left: 5px solid #4CAF50; padding: 15px; margin-bottom: 20px;">
            <h3 style="margin: 0; color: #2e7d32;">Нова книга додана до каталогу</h3>
        </div>

        <p>Назва: <b>${title}</b></p>
        <p>Автор: <b>${author}</b></p>
        <p>Рік: <b>${year?c}</b></p>

        <!-- Очищаємо від нецифрових символів (пробілів) перед перевіркою -->
        <#assign yearClean = (year?string)?replace("[^0-9]", "", "r")>
        <#if yearClean?has_content && yearClean?number < 2000>
            <p style="color:#a36b00; background-color: #fff8e1; padding: 10px; border: 1px solid #ffe082; border-radius: 4px;">
                <b>Раритетне видання!</b>
            </p>
        </#if>

        <p>Дата додавання: <b>${added?string("yyyy-MM-dd HH:mm")}</b></p>

        <!-- Кастомний футер -->
        <div style="margin-top: 30px; padding-top: 15px; border-top: 1px solid #eeeeee; text-align: center; font-size: 12px; color: #777;">
            <p>BookApp &copy; 2026</p>
        </div>
    </div>
</body>
</html>