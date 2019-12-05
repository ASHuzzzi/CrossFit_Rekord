# CrossFit Rekord

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
<a> <img src="https://user-images.githubusercontent.com/25584477/70217986-3e1c7c00-1753-11ea-953f-5293e787820f.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70217975-3ceb4f00-1753-11ea-934f-f214633087af.png"  height="283" width="160"> </a>
</p>

* записать пользователя на тренировку (с выбором дня и времени тренировки, но не далее чем за три дня до неё);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70218475-1aa60100-1754-11ea-95c0-e65cf75a746c.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70217973-3ceb4f00-1753-11ea-8c24-91142be9e523.png"  height="283" width="160"> </a>
</p>


* посмотреть комплексы прошедших тренировок. Внести свои результаты тренировки, а также посмотреть результаты других пользователей;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70217984-3e1c7c00-1753-11ea-9430-daa46c5905c8.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/70217982-3d83e580-1753-11ea-8dd0-94d34cbfd693.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/70217981-3d83e580-1753-11ea-946e-e7c227c8aa3f.png"  height="283" width="160"> </a>
</p>

* просмотр новостей от зала (пока только текст). Загрузка новостей происходит в фоне через сервис при старте приложения;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70217980-3d83e580-1753-11ea-926d-98c720912415.png"  height="283" width="160"> </a>
</p>


* предоставляет аналитику по базовым метрикам;
<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/69321561-6638b500-0c54-11ea-92da-7f9034c6e02a.png"  height="283" width="160"> </a>
</p>

* посмотреть/изменить данные пользователя;
* имеется небольшой словарь с терминами по кроссфиту (пока только в виде текста);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/70217979-3ceb4f00-1753-11ea-8a86-c051ba17f99c.png"  height="283" width="160"> </a>
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
