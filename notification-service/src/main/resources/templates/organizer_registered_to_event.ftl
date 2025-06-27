<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <style>
        body { font-family: Arial, sans-serif; color: #333; background: #f9f9f9; padding: 20px; }
        h2 { color: #2c3e50; }
        ul { list-style: none; padding: 0; }
        li { margin: 5px 0; }
        strong { color: #000; }
        .footer { margin-top: 20px; font-size: 0.9em; color: #777; }
    </style>
</head>
<body>
<h2>Здравствуйте, ${firstName}!</h2>
<p>Вы зарегистрированы <strong>как организатор</strong> мероприятия:</p>
<ul>
    <li><strong>Название:</strong> ${eventTitle}</li>
    <li><strong>Дата:</strong> ${eventDate}</li>
    <li><strong>Место:</strong> ${eventPlace}</li>
</ul>
<p>Организаторские материалы будут отправлены дополнительно.</p>
<div class="footer">
    Это письмо сгенерировано автоматически, пожалуйста не отвечайте на него.
</div>
</body>
</html>