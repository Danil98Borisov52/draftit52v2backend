<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Новое мероприятие</title>
    <style>
        body {
            font-family: "Segoe UI", Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f9f9f9;
            padding: 20px;
            color: #333;
        }
        .container {
            background: #fff;
            border-radius: 10px;
            max-width: 600px;
            margin: 0 auto;
            box-shadow: 0 0 10px rgba(0,0,0,0.1);
            padding: 30px;
        }
        h2 {
            color: #2b7cff;
            text-align: center;
        }
        table {
            width: 100%;
            border-spacing: 0;
            margin-top: 20px;
        }
        td {
            padding: 8px 0;
            vertical-align: top;
        }
        .label {
            font-weight: bold;
            color: #555;
            width: 150px;
        }
        .value {
            color: #222;
        }
        .footer {
            margin-top: 30px;
            text-align: center;
            font-size: 0.9em;
            color: #999;
        }
    </style>
</head>
<body>
<div class="container">
    <h2>📢 Новое мероприятие!</h2>
    <table>
        <tr><td class="label">📌 Название:</td><td class="value">${title}</td></tr>
        <tr><td class="label">📝 Описание:</td><td class="value">${description}</td></tr>
        <tr><td class="label">🗓 Дата начала:</td><td class="value">${startedAt}</td></tr>
        <tr><td class="label">📍 Место проведения:</td><td class="value">${place}</td></tr>
        <tr><td class="label">🏠 Адрес:</td><td class="value">${address}</td></tr>
        <#if addressComment?has_content>
            <tr><td class="label">💬 Комментарий:</td><td class="value">${addressComment}</td></tr>
        </#if>
        <tr><td class="label">👤 Автор:</td><td class="value">${authorName}</td></tr>
        <tr><td class="label">💰 Участие:</td><td class="value">${typePrice}</td></tr>
        <tr><td class="label">📄 Статус:</td><td class="value">${status}</td></tr>
        <tr><td class="label">🔗 Ссылка:</td><td class="value">${externalUrl! "не указана"}</td></tr>
        <#if tags?has_content>
            <tr><td class="label">🏷 Теги:</td><td class="value">${tags?join(", ")}</td></tr>
        </#if>
    </table>
    <div class="footer">
        Вы получили это сообщение, потому что подписаны на уведомления.
    </div>
</div>
</body>
</html>