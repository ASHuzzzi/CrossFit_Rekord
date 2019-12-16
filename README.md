# CrossFit Rekord

В данный момент это мой текущий pet project, находящийся в стадии разработки.

Стартовый экран приложения:

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885382-c61f4300-1fe9-11ea-9286-9c88a69a10a1.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/70885384-c6b7d980-1fe9-11ea-9174-007d2ea86785.png"  height="283" width="160"> </a>
</p>

### Что умеет приложение:
* авторизация/регистрация пользователя (через стандартные формы почта/пароль). 
* показывать расписание зала (на выбранный день недели);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885385-c6b7d980-1fe9-11ea-85d0-d5eedbe076b0.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885386-c6b7d980-1fe9-11ea-89da-d731c741e776.png"  height="283" width="160"> </a>
</p>

* записать пользователя на тренировку (с выбором дня и времени тренировки, но не далее чем за три дня до неё);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885387-c7507000-1fe9-11ea-92b4-ec8ff4f0c98b.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885388-c7507000-1fe9-11ea-999e-9c201595b82b.png"  height="283" width="160"> </a>
</p>


* посмотреть комплексы прошедших тренировок. Внести свои результаты тренировки, а также посмотреть результаты других пользователей;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885390-c7507000-1fe9-11ea-9ab4-4d06f58a9d01.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/70885392-c7507000-1fe9-11ea-8d77-bbddc0a11de2.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/70885393-c7e90680-1fe9-11ea-98c6-8f945cb347cb.png"  height="283" width="160"> </a>
</p>

* просмотр новостей от зала (пока только текст). Загрузка новостей происходит в фоне через сервис при старте приложения;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885394-c7e90680-1fe9-11ea-9de8-33b8675e190a.png"  height="283" width="160"> </a>
</p>


* предоставляет аналитику по базовым метрикам;
<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885396-c7e90680-1fe9-11ea-8652-a2f08a120e4a.png"  height="283" width="160"> </a>
</p>

* посмотреть/изменить данные пользователя;
* имеется небольшой словарь с терминами по кроссфиту (пока только в виде текста);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885397-c7e90680-1fe9-11ea-8c4e-6324cb7e16a6.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/70885398-c7e90680-1fe9-11ea-8164-09a7f303e425.png"  height="283" width="160"> </a>
</p>

* имеется форма для сохранения одноповторных максимумов основных упражнений по тяжелой атлетике;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70885399-c8819d00-1fe9-11ea-84c4-783b3e5ecc6a.png"  height="283" width="160"> </a>
</p>

В качестве бэкенда используется [Backendless](https://backendless.com/)

Касательно необходимости. Ни у одного из известных мне залов CrossFit нет приложения как такового. При этом у многих обязательна запись на тренировку, но она релизована не самым удобным способом (реальный пример: зайти в группу в вк, перейти по ссылке на сайт, выбрать день (страница перезагрузится полностью), выбрать время (снова перезагрузка страницы) и только теперь можно записаться).
Крайне сложно отслеживать результаты тренировки и, соответственно, анализировать их. В лучшем случае это фото в группе в вк.

На данный момент основные задачи приложения это:
* упростить запись в зал;
* оцифровать результаты тренировок;
* информировать клиента о новостях зала;
* избавление от "держания в голове" результатов одноповторных максимумов.

В качесте дополнительного функционала:
* можно настроить напоминания для записи на тренировку (т.к. мест ограниченное количество)
