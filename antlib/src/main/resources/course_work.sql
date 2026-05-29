-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Хост: 127.0.0.1
-- Время создания: Май 29 2026 г., 15:27
-- Версия сервера: 10.4.32-MariaDB
-- Версия PHP: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;

--
-- База данных: `course_work`
--
DROP DATABASE IF EXISTS `course_work`;
CREATE DATABASE `course_work` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci;
USE `course_work`;

-- --------------------------------------------------------

--
-- Структура таблицы `book_description`
--

CREATE TABLE `book_description` (
  `id` int(10) UNSIGNED NOT NULL,
  `title` varchar(255) NOT NULL,
  `author` varchar(255) NOT NULL,
  `year` int(11) DEFAULT NULL,
  `ISBN` varchar(255) DEFAULT NULL,
  `pages` int(11) DEFAULT NULL,
  `language` varchar(255) DEFAULT NULL,
  `publisher` varchar(255) DEFAULT NULL,
  `cover` text DEFAULT NULL,
  `description` text DEFAULT NULL,
  `verified` tinyint(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `book_description`
--

INSERT INTO `book_description` (`id`, `title`, `author`, `year`, `ISBN`, `pages`, `language`, `publisher`, `cover`, `description`, `verified`) VALUES
(3, 'Gone Girl', 'Gillian Flynn', 2012, '9780553398380', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/12272568-M.jpg', NULL, 1),
(6, 'Халцедоновый вереск', 'Вира Наперстянка', 2022, '9785992234770', 315, 'Русский', 'Альфа-книга', '//imo10.labirint.ru/books/876168/cover.jpg/484-0', 'Рия всегда смотрела на принца с восхищением. Даже когда он решил выгнать ее из замка. Но вот он уже король и вынужден устраивать отбор невест. Между ними пропасть из непонимания и противоборствующих стихий. Сможет ли Рия осуществить свою детскую мечту, если из общего у них только проведенная вместе ночь Судьбы и любовь к замку с неиссякаемыми тайнами?', 1),
(7, 'Мы вулканы', 'Наталья Преображенская', 2024, '9785907423893', 72, 'Русский', 'Абраказябра', '//imo10.labirint.ru/books/1016323/cover.jpg/484-0', 'С давних времён вулканы будоражат воображение человека. О них слагали мифы, сочиняли стихи и песни, им поклонялись. Сегодня вулканами занимается наука. Оказывается, они есть не только на земле, но и под водой и даже на далёких планетах! Почему вулканы просыпаются? Как предсказать их извержение? Из чего состоит лава и чем она отличается от магмы? Какую пользу вулканы приносят людям? Об этом и многом другом они расскажут вам сами! А ещё вулканы познакомят вас со своими знаменитыми собратьями. И помогут сделать домашний вулкан - безопасный! Для среднего школьного возраста.', 1),
(8, 'Мефистофель. История одной карьеры', 'Клаус Манн', 2022, '9785751617622', 384, NULL, 'Текст', '//imo10.labirint.ru/books/849544/cover.jpg/484-0', 'Самый известный роман Клауса Манна (1906-1949) - это трагическая история нравственного падения актера Хендрика Хёфгена, человека талантливого, но безвольного. В стремлении к славе и богатству Хёфген идет на сотрудничество с нацизмом, и карьера ему, казалось бы, обеспечена, но, полностью утратив духовную свободу в сетях властителей Третьего рейха, он начинает понимать, что совершил непоправимую ошибку - продал душу дьяволу. Роман был экранизирован в 1981 году режиссером Иштваном Сабо, фильм имел огромный успех. Физическая близость власти смущала и пугала его. Невероятная слава человека, сидевшего напротив, пугала честолюбца. А под незначительным покатым лбом власти, на который ниспадала легендарная сальная прядь, был мертвый, неподвижный, словно слепой взгляд. Лицо у власти было серо-белое, оплывшее, рыхлое и пористое. У власти был очень вульгарный нос - \"гнусный нос\", осмелился подумать Хендрик. К восторгу примешивалось чувство протеста, даже презрения. Артист заметил, что у власти вовсе нет затылка. Под коричневой рубашкой выступал мягкий живот. Говорила власть тихо, берегла свой осипший, хриплый голос. Она употребляла трудные слова, демонстрировала артисту \"образованность\". Клаус Манн', 1),
(9, '1984 (adaptation)', 'Michael Dean', 2003, '9780582777316', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/8745958-M.jpg', NULL, 1),
(12, 'Преступление и наказание', 'Фёдор Михайлович Достоевский', 1866, '9785280033023', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/9411873-M.jpg', NULL, 1),
(18, 'Harry Potter and the Philosopher\'s Stone', 'J. K. Rowling', 1997, '9785353002130', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/15155833-M.jpg', NULL, 1),
(21, 'Улыбочку, Красная Шапочка', 'Ришар Марнье, Од Морель', 2018, '9785001173816', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/8260789-M.jpg', NULL, 1),
(24, 'SMP 11-16', 'School Mathematics Project.', 1980, '9780521252126', NULL, NULL, NULL, NULL, NULL, 1),
(25, '9-11', 'Noam Chomsky', 2001, '9782842613235', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/6798220-M.jpg', NULL, 1),
(26, 'Harry Potter (series) 1-7', 'J. K. Rowling', 1999, '9780747544388', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/8457523-M.jpg', NULL, 1),
(27, 'Lolita', 'Vladimir Nabokov', 1777, '9785170081936', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/12984540-M.jpg', NULL, 1),
(28, 'Мертвая тишина', 'С. А. Барнс', 2024, '9785907500808', 512, 'Русский', 'Аркадия', '//imo10.labirint.ru/books/922824/cover.jpg/484-0', 'Капитан ремонтного корабля Клэр Ковалик совершает последний рейс, после которого ее ждет увольнение и в лучшем случае незавидная канцелярская работа. Когда уже пора отправляться в обратный путь, она ловит слабый сигнал бедствия и решает провести расследование. Ремонтников ждет шокирующее открытие: они обнаруживают \"Аврору\", роскошный космический лайнер, исчезнувший в первом же рейсе по Солнечной системе двадцать лет назад. Трупы оказываются не самым страшным, что ждет их на борту лайнера - им грозит та же ужасная участь, которая постигла пассажиров \"Авроры\", и Клэр оказывается единственной, кто еще может попытаться спасти остальных.', 1),
(30, 'Кто убил Оливию Коллинз?', 'Джо Спейн', 2020, '9785907143616', 416, NULL, 'Аркадия', '//imo10.labirint.ru/books/740146/cover.jpg/484-0', 'У Оливии Коллинз чудесный коттедж в маленьком элитном поселке. Эта милая женщина так хочет наладить хорошие отношения с соседями: дружить, ходить в гости, изливать душу и помогать в беде. Как же она ухитрилась со всеми перессориться? Наверное потому, что узнала грязные секреты и постыдные тайны своих внешне благопристойных соседей... И когда женщину находят убитой в собственном доме и полиция начинает расследование, выясняется, что ненавидят ее буквально все. Этот психологический детектив с прекрасно проработанными персонажами сдобрен острой приправой из тайн, обманов, измен и мести, и поражает совершенно неожиданным финалом.', 1),
(31, 'Эхо черного леса, Подолянин', 'Владимир Павлович Беляев', 2026, '9785002692545', 216, 'Русский', 'Родина', '//imo10.labirint.ru/books/1026606/cover.jpg/484-0', 'Захватывающая повесть о тайнах прошлого, раскрытии секретов и борьбе с врагами Родины. Действие происходит в первые послевоенные годы, когда Украина еще залечивала раны войны, а бывшие пособники нацистов продолжали свою преступную деятельность. Авторы воссоздают реальные события, когда чекисты вступают в смертельную схватку с агентурной сетью украинских националистов, оставшуюся от гитлеровской Германии. История основана на реальных событиях, однако авторы меняют детали и имена некоторых персонажей. Впервые опубликованная в 1963 году во время \"оттепели\", она больше не переиздавалась в СССР и в современной России. Причина: слишком реалистично показано противостояние чекистов и \"бандеровцев\". А так же правдиво рассказано о работе разведки и контрразведки.', 1),
(32, 'Похитители бриллиантов', 'Луи Анри Буссенар', 2022, '9785928733742', 626, 'Русский', 'Лабиринт', '//imo10.labirint.ru/books/843935/cover.jpg/484-0', 'Как часто вы задумываетесь о приключениях? О возможности совершить удивительное путешествие и, рискуя жизнью в дикой африканской природе, отыскать драгоценный клад? Не каждый может представить себя в ситуации главных героев этого романа - трех французов, которые случайно оказались в самом центре безумной охоты на таинственные и манящие сокровища кафрских королей. На пути к заветному кладу их ожидают тяжелые испытания, соперничество, романтическая любовь, предательство и смертельные опасности. Знаменитое произведение Луи Буссенара, признанного мастера приключенческого романа, проиллюстрировано его современником, французским художником Жюлем-Декартом Фера.', 1),
(33, 'Темные аллеи', 'Иван Алексеевич Бунин', 2018, '9785001120612', 352, NULL, 'Время', '//imo10.labirint.ru/books/586530/cover.jpg/484-0', 'В авторский сборник И. А. Бунина (1870-1953) \"Темные аллеи\" вошли его лирические новеллы, написанные с 1937 по 1949 год. В этих историях, объединенных вечной темой любви, не только представлена целая галерея пленительных женских портретов, но и выведена своего рода формула русской любви: в России 1910-х годов, в России накануне грозных исторических потрясений любовь неизменно оканчивается трагедией. \"Я тридцать восемь раз писал об одном и том же\", - говорил Бунин об этом цикле, который считал самым совершенным своим произведением и который до сих пор остается в числе главных отечественных книг о любви. Сопроводительная статья Елены Погорелой Погорелая Елена Алексеевна - поэт, литературный критик. Сфера научных интересов - русская поэзия XX-XXI веков, современная русская литература, возрастная психология и педагогика. Школьный и вузовский преподаватель, редактор отдела современности в журнале \"Вопросы литературы\" (2013)', 1),
(34, 'Грозовой перевал', 'Эмили Бронте', 2018, '9785001121015', 368, NULL, 'Время', '//imo10.labirint.ru/books/610569/cover.jpg/484-0', 'Перед нами единственный роман английской писательницы и поэтессы XIX века Эмили Бронте (1818-1848), средней и наиболее загадочной из знаменитых сестер Бронте. Он уверенно вошел в классику мировой литературы и остается востребованным вот уже более 150 лет. Волею автора сразу два рассказчика с первой страницы втягивают читателя в странную и жуткую историю обитателей двух соседних поместий, затерянных среди вересковых просторов Йоркшира. Тайная шкатулка, забытые письма, бурная, помутившая рассудок страсть кажутся началом захватывающего любовного романа, но чем дальше, тем страшнее становятся судьбы добропорядочных семейств. Соперничество между влюбленными в одну женщину героями перерастает в ненависть и непримиримую вражду с трагической развязкой. По мнению современника Э. Бронте поэта Данте Россетти, \"это дьявольская книга, немыслимое чудовище, объединившее все самые сильные женские наклонности\". В наши дни зрителями Британского телевидения \"Грозовой Перевал\" назван главной романтической книгой всех времен. Роман неоднократно экранизирован и переведен на многие языки. Перевод с английского Надежды Вольпин. Сопроводительная статья Елены Минкиной-Тайчер Минкина-Тайчер Елена Михайловна родилась и выросла в Москве. После окончания 1-го Московского мединститута работала врачом в отделении кардиореанимации одной из московских клиник. С 1991 года живет в Израиле и продолжает работать врачом. Автор трех книг прозы. Роман \"Эффект Ребиндера\" получил 2-е место в читательском голосовании премии \"Новая Словесность\" (2014), вошел в лонг-лист премии \"Русский Букер (2014), лонг-лист премии \"Национальный бестселлер\" (2015); роман \"Там, где течет молоко и мед\" - лонг-лист премии \"Ясная Поляна\" (2016); сборник повестей \"Женщина на заданную тему\" вышел в свет в 2017 году.', 1),
(35, 'Нашествие', 'Сергей Вадимович Казменко', 2025, '9785445904267', 592, 'Русский', 'Престиж БУК', '//imo10.labirint.ru/books/1025502/cover.jpg/484-0', '\"…Сергей Казменко способен сегодня предложить любому издателю материалы на два, а то и на три полновесных сборника самого профессионального уровня...\" Так писал Б. Н. Стругацкий в далеком 1990 году. И был абсолютно прав. Сергей Казменко ушёл из жизни в 1991 году, оставив значительное литературное наследие. Отдавая дань памяти писателю, издательство \"Престиж Бук\" подготовило \"Собрание сочинений\" в четырёх томах. В первый том вошли повесть \"Нашествие\" и несколько сборников рассказов, в которых ярко проявился талант автора - тонкий психологизм, философская глубина и мастерство художественного слова.', 1),
(36, 'Грамматика любви. Книга 1. Антология русского любовного рассказа, Станюкович, Немирович-Данченко', 'Лев Николаевич Толстой', 2025, '9785445904090', 704, 'Русский', 'Престиж БУК', '//imo10.labirint.ru/books/1025130/cover.jpg/484-0', 'В антологии русского любовного рассказа \"Грамматика любви\" впервые в таком объеме собраны произведения около 350 писателей конца XIX - первой трети XX столетия, начиная с признанных мастеров отечественной прозы - А. Чехова и М. Горького, И. Бунина и А. Куприна, Л. Андреева и Б. Зайцева... и заканчивая литературными \"аутсайдерами\" великой эпохи. По замыслу составителя, 500 рассказов: нежных, печальных, горьких, ироничных, порою жутких... должны стать настоящим Гимном Любви, какою бы она ни вышла из-под пера рассказчика, что бы ею ни двигало: страсть, преклонение, привычка, трезвый расчет, одержимость, самопожертвование... Это первая книга антологии. Составитель: Кудрявцев В.В.', 1),
(37, 'Люди живут семьями', 'Анатолий Борисович Данильченко', 2026, '9785448459290', 544, 'Русский', 'Вече', '//imo10.labirint.ru/books/1026563/cover.jpg/484-0', 'Каждую семью в нашей стране затронули беды Великой Отечественной войны. В каждой семье есть родня, чаще близкая, не вернувшаяся с той войны. Но семьи держались за свою кровь, даже если приходилось отдавать часть ее за страну. Потому что люди живут семьями - иначе не получается. Как в книге Анатолия Данильченко. Про время немецкой оккупации в белорусской деревеньке. Про судьбы и семьи, про добро и зло, про веру в победу, даже если эта вера ох как трудна. Ведь если не осталось ничего - осталась семья. А дружно - не грузно. Выдюжим. Отстроимся ещё.', 1),
(38, 'Островитянин. Идите с миром', 'Алексей Сергеевич Азаров', 2026, '9785002692484', 224, 'Русский', 'Родина', '//imo10.labirint.ru/books/1026601/cover.jpg/484-0', 'История советского разведчика болгарского происхождения Слави Багрянова. В начале Второй мировой войны под легендой коммерсанта из Софии, он ведет тайную деятельность на территориях Италии, Франции, Германии и собственной родины - Болгарии. Первая повесть сборника, \"Идите с миром\", описывает драматическое путешествие Багрянова, направленного с заданием доставить в столицу Третьего рейха ценнейшую шифровальную книгу и драгоценности. Преодолев многочисленные испытания, пройдя Италию и занятую нацистами Францию, герой достигает цели, демонстрируя стойкость и мужество перед лицом смертельной опасности. Вторая повесть, \"Островитянин\", раскрывает судьбу разведчика на родине, в Болгарии, которая была ареной ожесточенных сражений против фашистских захватчиков. Повесть насыщена напряженными эпизодами конспирации, опасностью разоблачения и примерами исключительного мужества советских агентов, работающих глубоко в тылу врага.', 1),
(39, 'Полное собрание романов в двух томах', 'Федор Михайлович Достоевский', 2023, '9785992203325', 1279, 'Русский', 'Альфа-книга', '//imo10.labirint.ru/books/188881/cover.jpg/484-0', 'Во второй том вошли романы \"Подросток\", \"Преступление и наказание\", \"Братья Карамазовы\".', 1),
(40, 'Пионовая беседка', 'Лиза Си', 2019, '9785906986023', 448, NULL, 'Аркадия', '//imo10.labirint.ru/books/613458/cover.jpg/484-0', 'Действие романа \"Пионовая беседка\" происходит в середине XVII века. Однажды в сердце юной девушки по имени Пион заглянула Любовь. Но вслед за ней пришла Смерть. И это стало для героини началом новой Жизни. Пожалуй, нет другого такого произведения, где в единое целое соединились бы реальная и загробная жизни, безграничная чувственность и стремление к высшей цели - потрясающее и незабываемое чтение! Популярная американская писательница Лиза Си - не только признанный знаток культурных традиций, обычаев и истории Китая, но и тонкий психолог, и мастер создания виртуозных сплетений сюжета. Ее конёк - уникальные женские судьбы, рассматриваемые сквозь призму китайской истории.', 1),
(41, 'Неточка Незванова. Кроткая', 'Федор Михайлович Достоевский', 2017, '9785926827139', 320, 'Русский', 'Речь', '//imo10.labirint.ru/books/628291/cover.jpg/484-0', '\"Неточка Незванова\" и \"Кроткая\" - произведения разных этапов творческого пути Ф. М. Достоевского. Незаконченный роман \"Неточка Незванова\" был создан в 1849 году, \"Кроткая\", определенная самим автором как \"фантастический рассказ\", написана в 1876 году. В \"Неточке Незвановой\" эскизно намечены ситуации и характеры будущих главных романов писателя, в \"Кроткой\" - тема \"униженных и оскорбленных\" оказывается исчерпанной и трагически завершенной. Эмоциональные, напряженные иллюстрации Михаила Ройтера дополняют текст и помогают прочувствовать его.', 1),
(42, 'Белые ночи', 'Федор Михайлович Достоевский', 2022, '9785907577077', 544, 'Русский', 'Галерея классики', '//imo10.labirint.ru/books/868246/cover.jpg/484-0', 'В сборник включены три ранних произведения Федора Михайловича Достоевского (1821-1881), писателя, мыслителя, публициста, классика мировой художественной литературы. \"Белые ночи\" - лирическая история романтических свиданий молодого Мечтателя и юной девушки. Их чистые и ясные души стремятся навстречу друг другу на фоне несравненного ночного Петербурга. Чувствительные и ранимые сердца героев романа \"Бедные люди\" и повести \"Неточка Незванова\" сталкиваются с жестокой действительностью и безжалостными законами, принятыми в обществе. В результате - разрушение идеалов, разочарование, страдание, одиночество и, быть может, смерть.', 1),
(43, 'Пользовательская книга', 'test', NULL, '', NULL, NULL, NULL, NULL, NULL, 0),
(44, '1984 (adaptation)', 'Michael Dean', 2003, '9780582777316', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/8745958-M.jpg', '123', 0),
(45, 'Белые ночи', 'Федор Михайлович Достоевский', 2018, '9785407009177', 64, 'Русский', 'Литера', '//imo10.labirint.ru/books/645197/cover.jpg/484-0', 'Сентиментальная повесть Ф. М. Достоевского рассказывает о романтической любви молодого мечтателя, одинокого и робкого человека, который однажды в белую ночь встретил и полюбил прекрасную девушку, почувствовав в ней родную душу. Книга адресована детям среднего и старшего школьного возраста.', 1),
(46, 'Harry Potter poster annual 2008', 'Warner Bros. Entertainment Inc', 2007, '9781405233101', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/10195373-M.jpg', NULL, 1),
(47, 'Chasing Lolita', 'Graham Vickers', 2008, '9781556526824', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/2908072-M.jpg', NULL, 1),
(48, 'Фауст', 'Иоганн Вольфганг Гете', 2018, '9785001120957', 480, NULL, 'Время', '//imo10.labirint.ru/books/610555/cover.jpg/484-0', 'Легенда о докторе Фаусте - один из корневых сюжетов мировой литературы. Из него выросло множество прекрасных творений - вспомним хотя бы Марло, Гуно, Томаса Манна, Пушкина, Булгакова… Но \"стволом\" фаустианы остается, несомненно, философская трагедия немецкого гения - Иоганна Вольфганга Гете (1749-1832). Причин тому немало. Во-первых, это великая поэзия. Во-вторых, это грандиозный по объему труд, на который ушло более тридцати лет. А, в-третьих, на извечный мучительный вопрос - имеет ли Зло Божественную санкцию? - именно Гете, не богослов, а художник, предложил наиболее убедительный ответ. Собственно, он прямо с этого ответа и начал: Мефистофель заключает пари с Господом - сможет ли мудрый и добрый профессор Фауст устоять перед искушением и спасти свою душу. Фауст перед Злом спасовал, но его философские искания, муки совести и раскаяние приносят ему спасение от ада - ангелы отбирают его душу у выигравшего пари Мефистофеля и уносят ее в рай. Великий и утешительный финал. Один из мефистофелей российской истории написал на книге Максима Горького известную рецензию \"Эта штука сильнее, чем \"Фауст\" Гете\". Злой дух в очередной раз ошибся. Книга неплохая, но \"Фауст\" остается непревзойденным. Перевод с немецкого Бориса Пастернака. Сопроводительная статья Юрия Арабова. Юрий Николаевич Арабов (р. 1954) - прозаик, поэт, сценарист. Выпускник ВГИКа, где ведет сейчас мастерскую драматургии и возглавляет кафедру кинодраматургии. Написал сценарии к более чем двадцати кинофильмам, постоянный соавтор Александра Сокурова. Автор нескольких романов и поэтических сборников. Заслуженный деятель искусств России, лауреат Государственной премии России и Премии Правительства России. Обладатель многих кинопремий, в том числе приза за лучший сценарий Каннского кинофестиваля.', 1),
(50, 'Wuthering Heights', 'Emily Brontë', 1846, '9785519549493', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/12818862-M.jpg', NULL, 1),
(51, 'Снежная королева', 'Ханс Кристиан Андерсен', 2023, '9785907142923', 56, NULL, 'Качели', '//imo10.labirint.ru/books/722618/cover.jpg/484-0', 'Подарочное издание самой известной сказки самого известного сказочника с иллюстрациями Патрика Джеймса Линча. Линч - современный художник, который сохраняет в иллюстрациях все богатство классического рисунка. Его персонажи оживают на страницах книг. Присмотритесь к Снежной королеве. Сколько ледяного могущества и снежной красоты обрела она под кистью современного классика. \"Снежная королева\" в серии \"Коллекция\" - это: - Характерные, полноцветные иллюстрации П. Д. Линча - Полный, без купюр, классический перевод - Коллекционный формат издания, позволяющий увидеть рисунки во всей их красоте; мелованная бумага, красочно оформленная обложка - Волшебная зимняя сказка, которую можно подарить и ребенку и взрослому на любой зимний праздник. Для детей среднего школьного возраста.', 1),
(52, 'You Are a Badass', 'Jen Sincero', 2013, '9780762490547', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/7436635-M.jpg', NULL, 1),
(53, 'Осторожно! Эта книга кусается!', 'Ник Бромли', 2017, '9785928728632', 28, 'Русский', 'Лабиринт', '//imo10.labirint.ru/books/630647/cover.jpg/484-0', 'Эта интерактивная книга начинает играть с читателем с первой же страницы: оказывается, что в сказку про гадкого утенка вдруг ни с того ни с сего пробрался… огромный крокодил! Крокодил безобразничает, рвёт страницы и глотает буквы - и читателю предстоит помочь утёнку справиться с этим хулиганом. Не так уж просто выбраться из книжки тому, что уже попал в неё, но крокодил находит удивительный способ! Книга не только смешит и удивляет - это история, в которой ребенок принимает непосредственное участие! Нужно то покачать книгу, то потрясти, восстановить слова, которые съел крокодил, - и даже если ребенок не очень любит читать, эту книгу он прочтет точно. А если ещё не умеет, то сможет разделить это приключение с родителями - и всем будет одинаково весело! Для детей 3-5 лет.', 1),
(54, 'My Family and other Animals', 'Gerald Malcolm Durrell', 1956, '9785227015488', NULL, NULL, NULL, 'https://covers.openlibrary.org/b/id/5547600-M.jpg', NULL, 1),
(56, 'Пользовательская книга', 'test', NULL, '', NULL, NULL, NULL, NULL, NULL, 0),
(62, 'Fahrenheit 451', 'Ray Bradbury', 2012, '9781451690316', 158, 'Английский', 'S&Sch USA', '//imo10.labirint.ru/books/794713/cover.jpg/484-0', 'Guy Montag is a fireman. In his world, where television rules and literature is on the brink of extinction, firemen start fires rather than put them out. His job is to destroy the most illegal of commodities, the printed book, along with the houses in which they are hidden. Montag never questions the destruction and ruin his actions produce, returning each day to his bland life and wife, Mildred, who spends all day with her television \"family.\" But then he meets an eccentric young neighbor, Clarisse, who introduces him to a past where people didn\'t live in fear and to a present where one sees the world through the ideas in books instead of the mindless chatter of television. When Mildred attempts suicide and Clarisse suddenly disappears, Montag begins to question everything he has ever known. He starts hiding books in his home, and when his pilfering is discovered, the fireman has to run for his life.', 1),
(69, 'The Three-Body Problem', 'Cixin Liu', 2016, '9781784971571', 442, 'Английский', 'Head of Zeus', '//imo10.labirint.ru/books/963660/cover.jpg/484-0', '1967: Ye Wenjie witnesses Red Guards beat her father to death during China`s Cultural Revolution. This singular event will shape not only the rest of her life but also the future of mankind. Four decades later, Beijing police ask nanotech engineer Wang Miao to infiltrate a secretive cabal of scientists after a spate of inexplicable suicides. Wang`s investigation will lead him to a mysterious online game and immerse him in a virtual world ruled by the intractable and unpredictable interaction of its three suns. This is the Three-Body Problem and it is the key to everything: the key to the scientists` deaths, the key to a conspiracy that spans light-years and the key to the extinction-level threat humanity now faces.', 1),
(70, 'Пользовательская книга', 'test', NULL, '', NULL, NULL, NULL, NULL, NULL, 0);

-- --------------------------------------------------------

--
-- Структура таблицы `collection`
--

CREATE TABLE `collection` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `description` text DEFAULT NULL,
  `last_update` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `collection`
--

INSERT INTO `collection` (`id`, `user_id`, `name`, `description`, `last_update`) VALUES
(10, 3, 'test', 'desc', '2025-05-26 00:00:00'),
(19, 4, '123', '123', '2026-05-27 18:23:56');

-- --------------------------------------------------------

--
-- Структура таблицы `collection_book`
--

CREATE TABLE `collection_book` (
  `collection_id` int(10) UNSIGNED NOT NULL,
  `user_book_mark_id` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `collection_book`
--

INSERT INTO `collection_book` (`collection_id`, `user_book_mark_id`) VALUES
(19, 43),
(19, 47),
(19, 48),
(19, 87);

-- --------------------------------------------------------

--
-- Структура таблицы `library`
--

CREATE TABLE `library` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `invite_code` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `library`
--

INSERT INTO `library` (`id`, `name`, `invite_code`) VALUES
(5, 'Дом', 'D94B870C39');

-- --------------------------------------------------------

--
-- Структура таблицы `room`
--

CREATE TABLE `room` (
  `id` int(10) UNSIGNED NOT NULL,
  `name` varchar(255) NOT NULL,
  `library_id` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `room`
--

INSERT INTO `room` (`id`, `name`, `library_id`) VALUES
(13, 'Зал', 5);

-- --------------------------------------------------------

--
-- Структура таблицы `shelf`
--

CREATE TABLE `shelf` (
  `id` int(10) UNSIGNED NOT NULL,
  `room_id` int(10) UNSIGNED NOT NULL,
  `width` int(11) NOT NULL,
  `height` int(11) NOT NULL,
  `position_x` int(11) NOT NULL,
  `position_y` int(11) NOT NULL,
  `capacity` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `shelf`
--

INSERT INTO `shelf` (`id`, `room_id`, `width`, `height`, `position_x`, `position_y`, `capacity`) VALUES
(19, 13, 40, 20, 3, 2, 30),
(24, 13, 60, 20, 4, 1, 45),
(25, 13, 40, 20, 6, 2, 30),
(27, 13, 20, 40, 1, 2, 15);

-- --------------------------------------------------------

--
-- Структура таблицы `shelf_book`
--

CREATE TABLE `shelf_book` (
  `id` int(10) UNSIGNED NOT NULL,
  `position` int(11) NOT NULL,
  `shelf_id` int(10) UNSIGNED NOT NULL,
  `book_description_id` int(10) UNSIGNED NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `shelf_book`
--

INSERT INTO `shelf_book` (`id`, `position`, `shelf_id`, `book_description_id`) VALUES
(44, 2, 19, 39),
(45, 3, 19, 40),
(46, 4, 19, 41),
(47, 1, 25, 37),
(48, 2, 25, 40),
(49, 1, 24, 8),
(50, 2, 24, 28),
(51, 3, 24, 35),
(52, 4, 24, 36),
(96, 1, 27, 8),
(97, 2, 27, 28),
(98, 3, 27, 35),
(99, 4, 27, 36),
(100, 5, 27, 37),
(101, 6, 27, 39),
(102, 7, 27, 40),
(103, 8, 27, 41),
(104, 5, 19, 43),
(110, 6, 19, 36);

-- --------------------------------------------------------

--
-- Структура таблицы `user`
--

CREATE TABLE `user` (
  `id` int(10) UNSIGNED NOT NULL,
  `username` varchar(255) NOT NULL,
  `password` varchar(255) NOT NULL,
  `email` varchar(255) NOT NULL,
  `role` varchar(255) NOT NULL,
  `date_join` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `user`
--

INSERT INTO `user` (`id`, `username`, `password`, `email`, `role`, `date_join`) VALUES
(3, 'user1', '$2a$10$0g6dynTHbOmE.FJQswqmcOIWaJoMi.PKjQKt5gbdpISkMyrSBJ53.', '1@gmail.com', 'ROLE_USER', '2026-05-25'),
(4, 'user2', '$2a$10$W9ogi8NNcAPG9kQzD/FgwOqciY4uXVukWhJkjnSAvzJUZ4ZNFrVme', '2@gmail.com', 'ROLE_USER', '2026-05-26'),
(5, 'user3', '$2a$10$apcvpZbbV7hatiaBk2QpUehzIagb1ow5y8eHKfIg.NFjINFSfucW.', '3@gmail.com', 'ROLE_USER', '2026-05-27');

-- --------------------------------------------------------

--
-- Структура таблицы `user_book_mark`
--

CREATE TABLE `user_book_mark` (
  `id` int(10) UNSIGNED NOT NULL,
  `user_id` int(10) UNSIGNED NOT NULL,
  `book_description_id` int(10) UNSIGNED NOT NULL,
  `rating` int(11) DEFAULT NULL,
  `source` enum('EBOOK','SHARED','OWNED','BORROWED') DEFAULT NULL,
  `status` enum('READING','PLANNED','POSTPONED','FINISHED') DEFAULT NULL,
  `date_start` date DEFAULT NULL,
  `date_finish` date DEFAULT NULL,
  `review` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `user_book_mark`
--

INSERT INTO `user_book_mark` (`id`, `user_id`, `book_description_id`, `rating`, `source`, `status`, `date_start`, `date_finish`, `review`) VALUES
(18, 3, 21, 4, 'BORROWED', NULL, NULL, NULL, ''),
(19, 3, 7, 3, 'SHARED', 'FINISHED', NULL, NULL, ''),
(20, 3, 8, NULL, 'OWNED', 'READING', NULL, NULL, ''),
(23, 3, 25, 4, 'EBOOK', 'POSTPONED', '2026-05-25', '2026-05-26', ''),
(24, 3, 26, 4, 'EBOOK', 'FINISHED', '2026-05-19', '2026-05-20', ''),
(25, 3, 27, NULL, NULL, NULL, NULL, NULL, ''),
(26, 3, 28, 1, 'OWNED', 'FINISHED', NULL, NULL, ''),
(28, 3, 30, 4, 'EBOOK', 'READING', NULL, NULL, ''),
(29, 3, 31, NULL, NULL, NULL, NULL, NULL, NULL),
(30, 3, 32, NULL, NULL, NULL, NULL, NULL, NULL),
(31, 3, 33, NULL, NULL, NULL, NULL, NULL, NULL),
(32, 3, 34, NULL, NULL, NULL, NULL, NULL, NULL),
(33, 3, 35, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(34, 3, 36, NULL, 'OWNED', 'POSTPONED', NULL, NULL, ''),
(35, 3, 37, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(36, 3, 38, 4, 'BORROWED', 'FINISHED', NULL, NULL, ''),
(37, 3, 39, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(38, 3, 40, NULL, 'OWNED', 'POSTPONED', NULL, NULL, ''),
(39, 3, 41, NULL, 'OWNED', 'FINISHED', NULL, NULL, ''),
(40, 3, 42, 5, 'BORROWED', 'PLANNED', NULL, NULL, ''),
(41, 4, 30, 2, 'EBOOK', 'FINISHED', NULL, NULL, ''),
(42, 4, 39, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(43, 4, 36, NULL, 'BORROWED', 'PLANNED', NULL, NULL, ''),
(44, 4, 18, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(45, 4, 31, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(46, 4, 25, 5, 'EBOOK', 'POSTPONED', NULL, NULL, '123123'),
(47, 4, 32, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(48, 4, 21, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(49, 4, 24, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(50, 4, 27, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(51, 4, 38, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(52, 4, 7, 5, 'SHARED', 'READING', NULL, NULL, ''),
(53, 4, 35, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(54, 4, 26, NULL, 'BORROWED', NULL, NULL, NULL, '123123'),
(55, 4, 40, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(56, 4, 8, NULL, 'SHARED', NULL, NULL, NULL, ''),
(57, 4, 44, NULL, 'EBOOK', 'READING', NULL, NULL, '123123123123123'),
(58, 4, 28, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(59, 4, 37, NULL, 'EBOOK', 'READING', NULL, NULL, ''),
(60, 4, 33, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(61, 4, 34, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(62, 4, 12, NULL, 'EBOOK', NULL, NULL, NULL, NULL),
(63, 3, 43, NULL, 'OWNED', 'FINISHED', NULL, NULL, ''),
(64, 4, 45, 4, 'BORROWED', 'FINISHED', NULL, NULL, ''),
(65, 4, 3, NULL, 'OWNED', NULL, NULL, NULL, '123123'),
(66, 3, 46, NULL, 'EBOOK', 'FINISHED', NULL, NULL, ''),
(67, 4, 48, 2, NULL, NULL, NULL, NULL, ''),
(68, 4, 46, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(70, 3, 50, NULL, NULL, NULL, NULL, NULL, ''),
(71, 4, 51, 3, 'BORROWED', NULL, NULL, NULL, ''),
(72, 4, 50, 2, 'OWNED', NULL, NULL, NULL, ''),
(73, 4, 53, NULL, 'BORROWED', NULL, NULL, NULL, ''),
(74, 4, 52, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(75, 3, 54, NULL, NULL, NULL, NULL, NULL, ''),
(77, 4, 54, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(78, 4, 56, NULL, 'SHARED', NULL, NULL, NULL, ''),
(87, 4, 69, NULL, 'BORROWED', NULL, NULL, NULL, ''),
(88, 4, 62, NULL, 'OWNED', NULL, NULL, NULL, NULL),
(89, 4, 70, NULL, 'SHARED', NULL, NULL, NULL, '');

-- --------------------------------------------------------

--
-- Структура таблицы `user_library`
--

CREATE TABLE `user_library` (
  `user_id` int(10) UNSIGNED NOT NULL,
  `library_id` int(10) UNSIGNED NOT NULL,
  `role` enum('OWNER','MEMBER') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_general_ci;

--
-- Дамп данных таблицы `user_library`
--

INSERT INTO `user_library` (`user_id`, `library_id`, `role`) VALUES
(3, 5, 'OWNER');

--
-- Индексы сохранённых таблиц
--

--
-- Индексы таблицы `book_description`
--
ALTER TABLE `book_description`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `collection`
--
ALTER TABLE `collection`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`);

--
-- Индексы таблицы `collection_book`
--
ALTER TABLE `collection_book`
  ADD PRIMARY KEY (`collection_id`,`user_book_mark_id`),
  ADD KEY `user_book_mark_id` (`user_book_mark_id`);

--
-- Индексы таблицы `library`
--
ALTER TABLE `library`
  ADD PRIMARY KEY (`id`);

--
-- Индексы таблицы `room`
--
ALTER TABLE `room`
  ADD PRIMARY KEY (`id`),
  ADD KEY `library_id` (`library_id`);

--
-- Индексы таблицы `shelf`
--
ALTER TABLE `shelf`
  ADD PRIMARY KEY (`id`),
  ADD KEY `room_id` (`room_id`);

--
-- Индексы таблицы `shelf_book`
--
ALTER TABLE `shelf_book`
  ADD PRIMARY KEY (`id`),
  ADD KEY `shelf_id` (`shelf_id`),
  ADD KEY `book_description_id` (`book_description_id`);

--
-- Индексы таблицы `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `username` (`username`);

--
-- Индексы таблицы `user_book_mark`
--
ALTER TABLE `user_book_mark`
  ADD PRIMARY KEY (`id`),
  ADD KEY `user_id` (`user_id`),
  ADD KEY `book_description_id` (`book_description_id`);

--
-- Индексы таблицы `user_library`
--
ALTER TABLE `user_library`
  ADD PRIMARY KEY (`user_id`,`library_id`),
  ADD KEY `library_id` (`library_id`);

--
-- AUTO_INCREMENT для сохранённых таблиц
--

--
-- AUTO_INCREMENT для таблицы `book_description`
--
ALTER TABLE `book_description`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=71;

--
-- AUTO_INCREMENT для таблицы `collection`
--
ALTER TABLE `collection`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=20;

--
-- AUTO_INCREMENT для таблицы `library`
--
ALTER TABLE `library`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT для таблицы `room`
--
ALTER TABLE `room`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT для таблицы `shelf`
--
ALTER TABLE `shelf`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=31;

--
-- AUTO_INCREMENT для таблицы `shelf_book`
--
ALTER TABLE `shelf_book`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=112;

--
-- AUTO_INCREMENT для таблицы `user`
--
ALTER TABLE `user`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT для таблицы `user_book_mark`
--
ALTER TABLE `user_book_mark`
  MODIFY `id` int(10) UNSIGNED NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=90;

--
-- Ограничения внешнего ключа сохраненных таблиц
--

--
-- Ограничения внешнего ключа таблицы `collection`
--
ALTER TABLE `collection`
  ADD CONSTRAINT `collection_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `collection_book`
--
ALTER TABLE `collection_book`
  ADD CONSTRAINT `collection_book_ibfk_1` FOREIGN KEY (`collection_id`) REFERENCES `collection` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `collection_book_ibfk_2` FOREIGN KEY (`user_book_mark_id`) REFERENCES `user_book_mark` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `room`
--
ALTER TABLE `room`
  ADD CONSTRAINT `room_ibfk_1` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `shelf`
--
ALTER TABLE `shelf`
  ADD CONSTRAINT `shelf_ibfk_1` FOREIGN KEY (`room_id`) REFERENCES `room` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `shelf_book`
--
ALTER TABLE `shelf_book`
  ADD CONSTRAINT `shelf_book_ibfk_1` FOREIGN KEY (`shelf_id`) REFERENCES `shelf` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `shelf_book_ibfk_2` FOREIGN KEY (`book_description_id`) REFERENCES `book_description` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `user_book_mark`
--
ALTER TABLE `user_book_mark`
  ADD CONSTRAINT `user_book_mark_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `user_book_mark_ibfk_2` FOREIGN KEY (`book_description_id`) REFERENCES `book_description` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;

--
-- Ограничения внешнего ключа таблицы `user_library`
--
ALTER TABLE `user_library`
  ADD CONSTRAINT `user_library_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION,
  ADD CONSTRAINT `user_library_ibfk_2` FOREIGN KEY (`library_id`) REFERENCES `library` (`id`) ON DELETE CASCADE ON UPDATE NO ACTION;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
