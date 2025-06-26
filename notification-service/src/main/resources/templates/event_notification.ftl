<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Новое мероприятие</title>
    <style>
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(to right, #f4f6f9, #e0e7ff);
            padding: 20px;
            color: #333;
        }

        .container {
            background: #fff;
            border-radius: 15px;
            max-width: 700px;
            margin: 0 auto;
            box-shadow: 0 10px 30px rgba(0,0,0,0.1);
            padding: 40px;
            position: relative;
        }

        h2 {
            color: #1a73e8;
            text-align: center;
            font-size: 28px;
            margin-bottom: 10px;
        }

        .subtitle {
            text-align: center;
            font-size: 18px;
            color: #555;
            margin-bottom: 30px;
        }

        .event-image {
            display: block;
            margin: 0 auto 30px auto;
            max-width: 100%;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
        }

        table {
            width: 100%;
            border-spacing: 0;
        }

        td {
            padding: 10px 0;
            vertical-align: top;
        }

        .label {
            font-weight: bold;
            color: #555;
            width: 180px;
        }

        .value {
            color: #222;
        }

        .button-container {
            text-align: center;
            margin-top: 40px;
        }

        .approve-button {
            background: #4CAF50;
            color: white !important;
            padding: 14px 28px;
            font-size: 16px;
            text-decoration: none;
            border-radius: 8px;
            display: inline-block;
            font-weight: bold;
        }

        .footer {
            margin-top: 40px;
            text-align: center;
            font-size: 0.9em;
            color: #888;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>🎉 Вас ждет нечто особенное!</h2>
    <div class="subtitle">Новое мероприятие, которое стоит вашего внимания</div>

    <#-- Если есть titleImage, показываем картинку -->
    <#if titleImageCid?has_content>
        <img src="cid:${titleImageCid}" alt="Обложка мероприятия" style="max-width:100%; height:auto;"/>
    </#if>

    <table>
        <tr><td class="label">📌 Название:</td><td class="value">${title}</td></tr>
        <tr><td class="label">📝 Описание:</td><td class="value">${description}</td></tr>
        <tr><td class="label">🗓 Дата начала:</td><td class="value">${startedAt}</td></tr>
        <tr><td class="label">📍 Место:</td><td class="value">${place}</td></tr>
        <tr><td class="label">🏠 Адрес:</td><td class="value">${address}</td></tr>
        <#if addressComment?has_content>
            <tr><td class="label">💬 Комментарий к адресу:</td><td class="value">${addressComment}</td></tr>
        </#if>
        <tr><td class="label">👤 Автор:</td><td class="value">${authorName}</td></tr>
        <tr><td class="label">💰 Участие:</td><td class="value">${typePrice}</td></tr>
        <tr><td class="label">📄 Статус:</td><td class="value">${status}</td></tr>
        <tr><td class="label">🔗 Ссылка:</td><td class="value">${externalUrl! "не указана"}</td></tr>
        <#if tags?has_content>
            <tr><td class="label">🏷 Теги:</td><td class="value">${tags?join(", ")}</td></tr>
        </#if>
    </table>

    <hr style="margin-top:30px;">

    <h3 style="color: #1a73e8;">ℹ️ Дополнительная информация</h3>
    <table>
        <tr><td class="label">📅 Создано:</td><td class="value">${createdAt}</td></tr>
        <tr><td class="label">🛠 Обновлено:</td><td class="value">${updatedAt}</td></tr>
        <tr><td class="label">🧾 Slug:</td><td class="value">${slug}</td></tr>
        <tr><td class="label">🎯 Тип (kind):</td><td class="value">${kind}</td></tr>
        <tr><td class="label">🌐 Foreign link:</td><td class="value">${foreignLink! "не указан"}</td></tr>
        <tr><td class="label">📊 Просмотры:</td><td class="value">${pageviews! "неизвестно"}</td></tr>
        <tr><td class="label">📢 Опубликовано:</td><td class="value">${published?string("Да", "Нет")}</td></tr>
    </table>

    <div class="button-container">
        <a class="approve-button" href="http://localhost:8081/api/events/${slug}/approve" target="_blank">✅ Одобрить мероприятие</a>
    </div>

    <div class="footer">
        Вы получили это сообщение, потому что подписаны на уведомления о мероприятиях.
    </div>
</div>
</body>
</html>