# CrossFit_Rekord

В данный момент это мой текущий pet project, находящийся в стадии разработки.

Стартовый экран приложения:

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/55539450-566e5f80-56c9-11e9-8143-e44938a3c1cb.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/56209520-75bba400-605c-11e9-92c3-66420dc50ccc.png"  height="283" width="160"> </a>
</p>

### Что уже умеет приложение:
* авторизация/регистрация пользователя (через стандартные формы почта/пароль). 
* показывать расписание зала (на выбранный день недели);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123633-f17a4500-0ab2-11ea-982e-f7d3523b32f4.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123634-f212db80-0ab2-11ea-90cf-7e6832342e0e.png"  height="283" width="160"> </a>
</p>

* записать пользователя на тренировку (с выбором дня и времени тренировки, но не далее чем за три дня до неё);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123643-f2ab7200-0ab2-11ea-9db0-de4a69dfb526.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123644-f3440880-0ab2-11ea-9aaf-24b9fda340a0.png"  height="283" width="160"> </a>
</p>


* посмотреть комплексы прошедших тренировок. Внести свои результаты тренировки, а также посмотреть результаты других пользователей;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123635-f212db80-0ab2-11ea-9257-ae0cddbb85db.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/69123636-f212db80-0ab2-11ea-9fc7-e441902df4b8.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/69123637-f212db80-0ab2-11ea-8faf-393d2854476a.png"  height="283" width="160"> </a>
</p>

* просмотр новостей от зала (пока только текст). Загрузка новостей происходит в фоне через сервис при старте приложения;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123638-f212db80-0ab2-11ea-8423-e80f36ca0929.png"  height="283" width="160"> </a>
</p>


* предоставляет аналитику по базовым метрикам;
<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69321561-6638b500-0c54-11ea-92da-7f9034c6e02a.png"  height="283" width="160"> </a>
</p>

* посмотреть/изменить данные пользователя;
* имеется небольшой словарь с терминами по кроссфиту (пока только в виде текста);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123640-f2ab7200-0ab2-11ea-8074-c079467b5687.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/69123642-f2ab7200-0ab2-11ea-8b57-bcc12ef5970d.png"  height="283" width="160"> </a>
</p>

* имеется форма для сохранения одноповторных максимумов основных упражнений по тяжелой атлетике;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69123639-f2ab7200-0ab2-11ea-9f5d-e8b1d6ea955b.png"  height="283" width="160"> </a>
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
