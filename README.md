# CrossFit_Rekord

В данный момент это мой текущий pet project, находящийся в стадии разработки.

Стартовый экран приложения:

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/55539450-566e5f80-56c9-11e9-8143-e44938a3c1cb.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/55539451-566e5f80-56c9-11e9-9373-4c645cad01f1.png"  height="283" width="160"> </a>
</p>

### Что уже умеет приложение:
* авторизация/регистрация пользователя (через стандартные формы почта/пароль). 
* показывать расписание зала (на выбранный день недели);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/55539453-566e5f80-56c9-11e9-9cdd-058e9bf2541f.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/53003990-42381100-3441-11e9-8946-d4f61963027e.png"  height="283" width="160"> </a>
</p>

* записать пользователя на тренировку (с выбором дня и времени тренировки, но не далее чем за три дня до неё);
<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/53003991-42381100-3441-11e9-86db-7eba87f66450.png"  height="283" width="160"> </a>
<align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/55539454-566e5f80-56c9-11e9-8928-70f238ddc367.png"  height="283" width="160"> </a>
</p>



* посмотреть комплексы прошедших тренировок. Внести свои результаты тренировки, а также посмотреть результаты других пользователей;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/55539455-566e5f80-56c9-11e9-87e7-1e6602e9a9fb.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/55539457-5706f600-56c9-11e9-87a8-f6a133dc83de.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/55539458-5706f600-56c9-11e9-808d-dc9778cc0774.png"  height="283" width="160"> </a>
</p>

* просмотр новостей от зала (пока только текст). Загрузка новостей происходит в фоне через сервис при старте приложения;

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/53003829-e8cfe200-3440-11e9-9fa8-774ed0b2cdd9.png"  height="283" width="160"> </a>
</p>

* посмотреть/изменить данные пользователя;
* имеется небольшой словарь с терминами по кроссфиту (пока только в виде текста);

<p align="center">
<a> <img src="https://user-images.githubusercontent.com/25584477/53003826-e8374b80-3440-11e9-95cf-27a39d9bd287.png"  height="283" width="160"> </a>
<a> <img src="https://user-images.githubusercontent.com/25584477/52460892-8c85dc00-2b7d-11e9-8de1-63e3b72a12d8.png"  height="283" width="160"> </a>
</p>

* имеется форма для сохранения одноповторных максимумов основных упражнений по тяжелой атлетике;

<p align="center">
<a> <img src="hhttps://user-images.githubusercontent.com/25584477/55539456-5706f600-56c9-11e9-9ce0-084e932895e7.png"  height="283" width="160"> </a>
</p>

### В процессе:
* тестирование приложения ограниченным количеством пользователей;
* работа по повышению стабильности приложения. Чистка и оптимизация кода.

### Cледующие задачи:
* доработка интерфейса;
* адаптивные макеты разметки;
* вывод приложения на общее тестирование.

В качестве бэкенда используется [Backendless](https://backendless.com/)

Касательно необходимости. Ни у одного из известных мне залов CrossFit нет приложения как такового. При этом у многих обязательна запись на тренировку, но она релизована не самым удобным способом (реальный пример: зайти в группу в вк, перейти по ссылке на сайт и только там уже записаться).
Крайне сложно отслеживать результаты тренировки и, соответственно, анализировать их. В лучшем случае это фото в группе в вк.

На данный момент основные задачи приложения это:
* упростить запись в зал;
* оцифровать результаты тренировок;
* информировать клиента о новостях зала;
* избавление от "держания в голове" результатов одноповторных максимумов.
