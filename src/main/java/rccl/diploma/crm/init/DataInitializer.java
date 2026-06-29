package rccl.diploma.crm.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import rccl.diploma.crm.entity.*;
import rccl.diploma.crm.entity.enums.*;
import rccl.diploma.crm.repository.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Сидер тестовых данных.
 * Запускается при старте приложения. Если в БД уже больше 50 пользователей — пропускается.
 *
 * Учётные данные:
 *   admin  / admin
 *   master / master  (7 мастеров, username — см. MASTERS)
 *   res    / res     (95 жильцов, username — фамилия_транслит + индекс)
 */
@Configuration
public class DataInitializer {

    // ══════════════════════════════════ СПРАВОЧНИКИ ════════════════════════════════

    private static final String[] BUILDINGS = {
        "ул. Ленина, 12",       "ул. Ленина, 14",
        "пр. Мира, 7",          "пр. Мира, 22",
        "ул. Советская, 3",     "ул. Советская, 15",
        "ул. Пушкина, 8",       "ул. Гагарина, 19",
        "пр. Победы, 11",       "ул. Садовая, 5",
        "ул. Новая, 2",         "пр. Строителей, 33"
    };

    // ── Мастера ───────────────────────────────────────────────────────────────────
    // {фамилия, имя, отчество, логин}
    private static final String[][] MASTERS = {
        {"Соловьёв",  "Игорь",      "Петрович",       "master"},
        {"Громов",    "Виталий",    "Николаевич",     "gromov_master"},
        {"Беляев",    "Константин", "Вячеславович",   "belyaev_master"},
        {"Ситников",  "Роман",      "Иванович",       "sitnikov_master"},
        {"Чернов",    "Вадим",      "Олегович",       "chernov_master"},
        {"Гусев",     "Станислав",  "Андреевич",      "gusev_master"},
        {"Лазарев",   "Евгений",    "Борисович",      "lazarev_master"},
    };

    // ── Мужские ФИО ───────────────────────────────────────────────────────────────
    private static final String[] M_SURNAMES = {
        "Иванов", "Смирнов", "Кузнецов", "Попов", "Васильев",
        "Петров", "Соколов", "Михайлов", "Новиков", "Фёдоров",
        "Морозов", "Волков", "Алексеев", "Лебедев", "Семёнов",
        "Егоров", "Павлов", "Козлов", "Степанов", "Николаев",
        "Орлов", "Андреев", "Макаров", "Никитин", "Захаров"
    };
    private static final String[] M_TRANSLIT = {
        "ivanov", "smirnov", "kuznetsov", "popov", "vasilev",
        "petrov", "sokolov", "mihajlov", "novikov", "fedorov",
        "morozov", "volkov", "alekseev", "lebedev", "semenov",
        "egorov", "pavlov", "kozlov", "stepanov", "nikolaev",
        "orlov", "andreev", "makarov", "nikitin", "zaharov"
    };
    private static final String[] M_NAMES = {
        "Александр", "Михаил", "Дмитрий", "Сергей", "Андрей",
        "Алексей", "Максим", "Евгений", "Игорь", "Николай",
        "Владимир", "Павел", "Артём", "Иван", "Антон"
    };
    private static final String[] M_PATRO = {
        "Александрович", "Михайлович", "Дмитриевич", "Сергеевич", "Андреевич",
        "Алексеевич", "Максимович", "Евгеньевич", "Игоревич", "Николаевич"
    };

    // ── Женские ФИО ───────────────────────────────────────────────────────────────
    private static final String[] F_SURNAMES = {
        "Иванова", "Смирнова", "Кузнецова", "Попова", "Васильева",
        "Петрова", "Соколова", "Михайлова", "Новикова", "Фёдорова",
        "Морозова", "Волкова", "Алексеева", "Лебедева", "Семёнова",
        "Егорова", "Павлова", "Козлова", "Степанова", "Николаева",
        "Орлова", "Андреева", "Макарова", "Никитина", "Захарова"
    };
    private static final String[] F_TRANSLIT = {
        "ivanova", "smirnova", "kuznetsova", "popova", "vasileva",
        "petrova", "sokolova", "mihajlova", "novikova", "fedorova",
        "morozova", "volkova", "alekseeva", "lebedeva", "semenova",
        "egorova", "pavlova", "kozlova", "stepanova", "nikolaeva",
        "orlova", "andreeva", "makarova", "nikitina", "zaharova"
    };
    private static final String[] F_NAMES = {
        "Анна", "Мария", "Елена", "Ольга", "Наталья",
        "Татьяна", "Ирина", "Екатерина", "Светлана", "Юлия",
        "Людмила", "Нина", "Галина", "Валентина", "Алина"
    };
    private static final String[] F_PATRO = {
        "Александровна", "Михайловна", "Дмитриевна", "Сергеевна", "Андреевна",
        "Алексеевна", "Максимовна", "Евгеньевна", "Игоревна", "Николаевна"
    };

    // ── Заголовки заявок по типу ─────────────────────────────────────────────────
    private static final Map<RequestType, String[]> TITLES;
    static {
        TITLES = new EnumMap<>(RequestType.class);
        TITLES.put(RequestType.HEATING_ISSUES,    new String[]{
            "Не греют батареи в комнате", "Слабое отопление в квартире",
            "Холодные радиаторы", "Температура в квартире ниже нормы"});
        TITLES.put(RequestType.HOT_WATER_ISSUES,  new String[]{
            "Нет горячей воды", "Горячая вода еле тёплая",
            "Плохой напор ГВС", "Перебои с горячей водой"});
        TITLES.put(RequestType.COLD_WATER_ISSUES, new String[]{
            "Нет холодной воды", "Слабый напор ХВС",
            "Шум в трубах ХВС", "Протечка крана холодной воды"});
        TITLES.put(RequestType.SEWERAGE_ISSUES,   new String[]{
            "Засор канализации", "Неприятный запах из канализации",
            "Авария в стояке канализации", "Протечка канализационной трубы"});
        TITLES.put(RequestType.ELEVATOR_ISSUES,   new String[]{
            "Лифт не работает", "Лифт застревает между этажами",
            "Шум и вибрация в лифте", "Дверь лифта не закрывается"});
        TITLES.put(RequestType.LIGHTING_ISSUES,   new String[]{
            "Не горит свет в подъезде", "Перегорела лампа на площадке",
            "Мигает освещение в лифтовом холле", "Нет света в подвале"});
        TITLES.put(RequestType.ROOF_LEAKAGE,      new String[]{
            "Протечка кровли", "Затопило квартиру сверху",
            "Протечка водостока у окна", "Подтёк потолок после дождя"});
        TITLES.put(RequestType.VENTILATION_ISSUES,new String[]{
            "Не работает вытяжка на кухне", "Плохая тяга в вентиляции",
            "Запах дыма из вентиляционной решётки", "Засорен вентканал"});
        TITLES.put(RequestType.GAS_EQUIPMENT,     new String[]{
            "Запах газа в квартире", "Неисправная газовая плита",
            "Требуется проверка газового счётчика", "Засорился газовый котёл"});
        TITLES.put(RequestType.BUILDING_STRUCTURE,new String[]{
            "Трещина в стене подъезда", "Разрушение отмостки у дома",
            "Протечка в подвале", "Повреждение фасада здания"});
        TITLES.put(RequestType.ELECTRICITY_ISSUES,new String[]{
            "Нет электричества в квартире", "Постоянно выбивает автомат",
            "Искрение розеток", "Перепады напряжения в сети"});
        TITLES.put(RequestType.COMMON_AREA_MAINTENANCE, new String[]{
            "Грязь и мусор в подъезде", "Вандализм в МОП",
            "Сломана входная дверь подъезда", "Неубранный снег у входа"});
        TITLES.put(RequestType.BILLING_ERRORS,    new String[]{
            "Ошибка в начислениях за ЖКУ", "Некорректный ЕПД за этот месяц",
            "Двойное начисление по счётчику", "Прошу провести перерасчёт"});
        TITLES.put(RequestType.MANAGEMENT_COMPLAINTS, new String[]{
            "Жалоба на качество обслуживания", "Вопрос по протоколу ОСС",
            "Претензия к подрядчику УК", "Запрос информации по смете"});
        TITLES.put(RequestType.OTHER,             new String[]{
            "Другая проблема", "Прочее обращение", "Вопрос к управляющему"});
    }

    // ── Шаблоны комментариев ─────────────────────────────────────────────────────
    private static final String[] MASTER_COMMENTS = {
        "Осмотрел, установил причину неисправности. Приступаю к устранению.",
        "Первичная диагностика выполнена. Требуется замена расходного материала.",
        "Работа выполнена в полном объёме. Проверил работоспособность.",
        "Устранил неисправность. Жилец проинформирован, претензий нет.",
        "Выполнен ремонт, дополнительно осмотрел смежные узлы — всё в норме.",
        "Причина установлена: износ уплотнителя. Произведена замена.",
        "Работы выполнены. Рекомендую плановую профилактику через 6 месяцев.",
        "Аварийная ситуация устранена. Сантехнический узел герметизирован.",
        "Проверка выполнена, неисправность подтверждена. Составил акт осмотра.",
        "Оборудование заменено. Протечки устранены, давление в норме.",
        "Прибыл на место. Жильца нет дома — согласую повторный визит.",
        "Требуется дополнительный материал. Закажу — приеду послезавтра.",
    };
    private static final String[] RESIDENT_COMMENTS = {
        "Когда ждать мастера? Проблема никуда не делась.",
        "Прошу ускорить рассмотрение — ситуация ухудшается.",
        "Спасибо, мастер отработал профессионально.",
        "Всё исправлено, претензий нет. Благодарю.",
        "Можно уточнить сроки? Удобное время — с 9 до 13.",
        "Проблема повторилась снова после ремонта.",
        "Подтверждаю — всё работает нормально.",
        "Мастер был, посмотрел, но ничего не сделал.",
    };
    private static final String[] ADMIN_COMMENTS = {
        "Заявка принята, поставлена в очередь исполнения.",
        "Мастер будет направлен в ближайшее рабочее время.",
        "Уточните, пожалуйста, удобное время для визита.",
        "Заявка переведена в приоритетную очередь.",
        "Выполнение подтверждено администрацией. Спасибо за обращение.",
        "Передаю заявку дежурному мастеру.",
        "Аварийная бригада уже в пути.",
    };
    private static final String[] DESCRIPTIONS = {
        "Прошу рассмотреть обращение. Проблема возникла несколько дней назад и не исчезает.",
        "Ситуация ухудшается, прошу ускорить рассмотрение и направить специалиста.",
        "Данная неисправность мешает нормальному проживанию. Прошу устранить.",
        "Прошу направить мастера в удобное для меня время. Готов оставить ключи соседям.",
        "Жду решения в кратчайшие сроки. Проблема уже доставляет значительные неудобства.",
        "Обращался ранее — проблема не устранена полностью. Прошу повторный визит.",
        "Ситуация требует срочного вмешательства. Прошу рассмотреть в приоритетном порядке.",
        "Могу предоставить фотографии по запросу. Готов к любому времени визита.",
    };

    private static final Object[][] NEWS_DATA = {
            {"Плановое отключение горячей воды", "С 9:00 до 17:00 будет отключена горячая вода в связи с плановыми ремонтными работами на теплотрассе.", NewsCategory.RENOVATION},
            {"Покраска подъезда №1 завершена", "Все работы выполнены в срок. Просим жильцов соблюдать чистоту в подъезде.", NewsCategory.RENOVATION},
            {"Собрание жильцов 30 июня", "В 19:00 в холле первого этажа состоится общее собрание. Повестка: ремонт кровли и установка домофонов.", NewsCategory.EVENT},
            {"Замена лифтового оборудования", "С понедельника по пятницу лифт будет недоступен с 10:00 до 14:00. Приносим извинения за неудобства.", NewsCategory.RENOVATION},
            {"Уборка придомовой территории", "Субботник состоится 15 июня в 10:00. Просим всех желающих присоединиться.", NewsCategory.EVENT},
            {"Новый мастер по сантехнике", "В штат управляющей компании принят новый специалист по сантехнике. Заявки принимаются через портал.", NewsCategory.ANNOUNCEMENT},
            {"Установка видеонаблюдения", "В подъездах и на парковке установлены камеры видеонаблюдения для повышения безопасности.", NewsCategory.ANNOUNCEMENT},
            {"Ремонт козырька над входом", "Ремонтные работы продлятся 3 дня. Просим использовать запасной вход со стороны двора.", NewsCategory.RENOVATION},
            {"Тарифы на коммунальные услуги", "С 1 июля вступают в силу новые тарифы на водоснабжение и водоотведение согласно постановлению.", NewsCategory.MONEY},
            {"Дезинфекция подвальных помещений", "Обработка состоится 20 июня. Просим убрать вещи из подвала заблаговременно.", NewsCategory.HEALTHCARE},
            {"Проверка газового оборудования", "Специалисты газовой службы посетят квартиры 22–23 июня с 10:00 до 16:00. Просим обеспечить доступ.", NewsCategory.WARNING},
            {"Новые урны во дворе", "Установлены 6 новых урн для раздельного сбора мусора. Просим соблюдать правила сортировки.", NewsCategory.ANNOUNCEMENT},
            {"Ремонт асфальтового покрытия", "Ямочный ремонт во дворе запланирован на 18 июня. Парковка будет временно ограничена.", NewsCategory.RENOVATION},
            {"Перебои с электроснабжением", "В связи с работами на подстанции 19 июня с 13:00 до 15:00 возможны кратковременные отключения.", NewsCategory.DANGER},
            {"Установка новых почтовых ящиков", "В подъезде №2 смонтированы новые почтовые ящики. Ключи можно получить в управляющей компании.", NewsCategory.ANNOUNCEMENT},
            {"Опрос жильцов о благоустройстве", "Просим пройти короткий опрос о пожеланиях по благоустройству двора на стенде в подъезде.", NewsCategory.ANNOUNCEMENT},
            {"Акция по замене счётчиков", "До конца месяца действует льготная замена счётчиков воды. Заявки через портал.", NewsCategory.MONEY},
            {"Ремонт системы отопления", "Гидравлические испытания теплосети пройдут 25–26 июня. Отопление будет отключено.", NewsCategory.RENOVATION},
            {"Новая детская площадка", "Монтаж игрового комплекса во дворе завершится к 1 июля. Спасибо за терпение.", NewsCategory.ANNOUNCEMENT},
            {"Вывоз крупногабаритного мусора", "Специализированная машина приедет 21 июня в 11:00. Выставляйте крупный мусор к 10:30.", NewsCategory.ANNOUNCEMENT},
            {"Антенное оборудование на крыше", "Технические работы на крыше 17 июня могут вызвать кратковременные помехи телесигнала.", NewsCategory.WARNING},
            {"Итоги голосования по парковке", "Большинство жильцов проголосовало за нанесение разметки. Работы запланированы на июль.", NewsCategory.ANNOUNCEMENT},
            {"Профилактика домофонов", "Техническое обслуживание домофонной системы 16 июня с 12:00 до 14:00.", NewsCategory.RENOVATION},
            {"Новый график вывоза мусора", "С 1 июля мусор будет вывозиться ежедневно в 7:00 и 19:00. Просим не выставлять пакеты заранее.", NewsCategory.ANNOUNCEMENT},
            {"Ремонт кровли завершён", "Все работы по замене кровельного покрытия выполнены. Гарантия 5 лет.", NewsCategory.RENOVATION},
            {"Озеленение двора", "Высажены новые кустарники и цветники вдоль дорожек. Просим не топтать газон.", NewsCategory.ANNOUNCEMENT},
            {"Смена управляющей компании", "С 1 августа обслуживанием дома займётся УК «Комфорт». Реквизиты для оплаты изменятся.", NewsCategory.ANNOUNCEMENT},
            {"Установка шлагбаума", "Въезд во двор будет оборудован шлагбаумом. Пульты распределит УК до 30 июня.", NewsCategory.ANNOUNCEMENT},
            {"Ремонт освещения на лестницах", "Заменены все лампы в подъездах на энергосберегающие светодиодные.", NewsCategory.RENOVATION},
            {"Страхование общего имущества", "Полис страхования общедомового имущества продлён на следующий год.", NewsCategory.MONEY},
            {"Отчёт УК за первый квартал", "Финансовый отчёт управляющей компании размещён на информационном стенде в подъезде.", NewsCategory.MONEY},
            {"Проверка пожарной безопасности", "Инспекция МЧС состоится 24 июня. Просим не загромождать пути эвакуации.", NewsCategory.WARNING},
            {"Ремонт мусоропровода", "Мусоропровод временно закрыт на дезинфекцию. Мусор просим выносить к контейнерам во дворе.", NewsCategory.RENOVATION},
            {"Новые правила парковки", "С 1 июля парковка на газоне запрещена. Нарушителей будет фиксировать видеокамера.", NewsCategory.WARNING},
            {"Конкурс на лучший балкон", "Принимаем заявки на конкурс «Лучший балкон — 2024». Призы от управляющей компании.", NewsCategory.EVENT},
            {"Замена труб в подвале", "Плановая замена стояков ХВС в подвале 23–24 июня. Холодная вода будет отключена.", NewsCategory.RENOVATION},
            {"Установка пандуса", "По просьбе жильцов у главного входа смонтирован пандус для маломобильных граждан.", NewsCategory.ANNOUNCEMENT},
            {"Ремонт системы вентиляции", "Прочистка вентиляционных каналов состоится 19–20 июня. Доступ в квартиры не требуется.", NewsCategory.RENOVATION},
            {"Праздник двора 12 июня", "Приглашаем всех жильцов на праздник в честь Дня России. Начало в 15:00 во дворе.", NewsCategory.EVENT},
            {"Новый способ оплаты ЖКУ", "Теперь оплатить квитанции можно через Систему быстрых платежей без комиссии.", NewsCategory.MONEY},
            {"Предупреждение о мошенниках", "Участились случаи мошенничества под видом сотрудников УК. Требуйте удостоверение.", NewsCategory.DANGER},
            {"Ремонт фасада", "Штукатурные работы на фасаде начнутся 1 июля и продлятся около двух недель.", NewsCategory.RENOVATION},
            {"Горячая линия УК", "Открыта круглосуточная горячая линия для экстренных вызовов: 8-800-XXX-XX-XX.", NewsCategory.ANNOUNCEMENT},
            {"Перенос трансформаторной будки", "Работы по переносу ТП запланированы на конец июня. Возможны кратковременные отключения.", NewsCategory.DANGER},
            {"Ремонт водосточных труб", "Специалисты проведут осмотр и ремонт водосточной системы 18 июня.", NewsCategory.RENOVATION},
            {"Новый подрядчик по уборке", "С июля уборку подъездов и прилегающей территории будет выполнять ООО «Чистый дом».", NewsCategory.ANNOUNCEMENT},
            {"Установка велопарковки", "У главного входа смонтирована велопарковка на 10 мест. Приятных поездок!", NewsCategory.ANNOUNCEMENT},
            {"Итоги субботника", "Благодарим 34 жильцов, принявших участие в субботнике. Собрано 12 мешков мусора.", NewsCategory.EVENT},
            {"Ремонт системы видеонаблюдения", "14–15 июня камеры будут временно отключены на время технического обслуживания.", NewsCategory.RENOVATION},
            {"Поздравление с Днём соседей", "УК «Уют» поздравляет всех жильцов с Международным днём соседей! Будьте внимательны друг к другу.", NewsCategory.EVENT},
    };

    // ═══════════════════════════════════ БИН СИДЕРА ════════════════════════════════

    @Bean
    CommandLineRunner seedAll(
            UserRepository             userRepo,
            BuildingRepository         buildingRepo,
            RequestRepository          requestRepo,
            RequestCommentRepository   commentRepo,
            BalanceTransactionRepository txRepo,
            MeterReadingRepository     meterRepo,
            NewsRepository             newsRepo,
            PasswordEncoder            passwordEncoder) {

        return args -> {

            // Пропустить, если БД уже засеяна полными данными
            if (userRepo.count() >= 50) return;

            // Очистка старых тестовых данных (в порядке зависимостей FK)
            meterRepo.deleteAll();
            txRepo.deleteAll();
            commentRepo.deleteAll();
            requestRepo.deleteAll();
            userRepo.deleteAll();
            buildingRepo.deleteAll();

            Random rnd = new Random(42); // фиксированный seed = воспроизводимые данные
            LocalDateTime now = LocalDateTime.now();

            // ── 1. Дома ─────────────────────────────────────────────────────────
            List<Building> buildings = new ArrayList<>();
            for (String addr : BUILDINGS) {
                buildings.add(buildingRepo.save(Building.builder().address(addr).build()));
            }

            // ── 2. Администратор ────────────────────────────────────────────────
            User admin = userRepo.save(User.builder()
                .username("admin").password(passwordEncoder.encode("admin"))
                .email("admin@zhkh-portal.ru")
                .name("Максим").surname("Сосна").lastName("Сергеевич")
                .role(Role.ADMIN).enabled(true).build());

            // ── 3. Мастера (7 человек) ──────────────────────────────────────────
            List<User> masters = new ArrayList<>();
            for (int i = 0; i < MASTERS.length; i++) {
                String[] m = MASTERS[i];
                masters.add(userRepo.save(User.builder()
                    .username(m[3]).password(passwordEncoder.encode("mas"))
                    .email(m[3] + "@zhkh-portal.ru")
                    .surname(m[0]).name(m[1]).lastName(m[2])
                    .phone(genPhone(i))
                    .role(Role.MASTER).enabled(true).build()));
            }

            // ── 4. Жильцы (95 человек) ──────────────────────────────────────────
            List<User> residents = new ArrayList<>();
            int phoneIdx = masters.size(); // продолжаем индекс телефонов после мастеров

            for (int i = 0; i < 95; i++) {
                boolean male = i % 2 == 0;
                String[] surnames  = male ? M_SURNAMES  : F_SURNAMES;
                String[] translits = male ? M_TRANSLIT  : F_TRANSLIT;
                String[] firstNms  = male ? M_NAMES     : F_NAMES;
                String[] patros    = male ? M_PATRO      : F_PATRO;

                String surname   = surnames [i % surnames.length];
                String firstName = firstNms [i % firstNms.length];
                String patro     = patros   [i % patros.length];
                String translit  = translits[i % translits.length];
                String username  = translit + "_" + i;

                Building building = buildings.get(i % buildings.size());
                int apartment = 1 + (i % 150); // кв. 1–150

                residents.add(userRepo.save(User.builder()
                    .username(username).password(passwordEncoder.encode("res"))
                    .email(username + "@mail.ru")
                    .surname(surname).name(firstName).lastName(patro)
                    .phone(genPhone(phoneIdx++))
                    .building(building).apartment(String.valueOf(apartment))
                    .role(Role.RESIDENT).enabled(true).build()));
            }

            // ── 5. Заявки (~260 штук за 60 дней) ────────────────────────────────
            //
            // Распределение статусов (веса):
            //   NEW=20, IN_PROGRESS=18, PENDING_REVIEW=12, DONE=35,
            //   REJECTED=8, CANCELLED=3, ON_HOLD=4
            //
            // Enum-порядок: NEW, IN_PROGRESS, PENDING_REVIEW, DONE, REJECTED, CANCELLED, ON_HOLD
            RequestStatus[] statusPool = buildWeightedPool(
                RequestStatus.values(), new int[]{20, 18, 12, 35, 8, 3, 4});

            RequestType[] allTypes = RequestType.values();
            List<Request> requests = new ArrayList<>();

            for (int i = 0; i < 260; i++) {
                User resident = residents.get(rnd.nextInt(residents.size()));
                RequestType type   = allTypes[rnd.nextInt(allTypes.length)];
                RequestStatus status = statusPool[rnd.nextInt(statusPool.length)];

                // Дата создания — случайно в диапазоне [now-60d, now-1h]
                int daysAgo = 1 + rnd.nextInt(59);
                LocalDateTime createdAt = now
                    .minusDays(daysAgo)
                    .minusHours(rnd.nextInt(20))
                    .minusMinutes(rnd.nextInt(60));

                // Назначение мастера: у NEW — нет, у CANCELLED — 50/50, у остальных — есть
                User master = null;
                if (status != RequestStatus.NEW) {
                    if (status != RequestStatus.CANCELLED || rnd.nextBoolean()) {
                        master = masters.get(rnd.nextInt(masters.size()));
                    }
                }

                // Дата закрытия: у DONE, REJECTED, CANCELLED
                LocalDateTime closedAt = null;
                if (status == RequestStatus.DONE
                        || status == RequestStatus.REJECTED
                        || status == RequestStatus.CANCELLED) {
                    closedAt = createdAt.plusDays(1 + rnd.nextInt(6)).plusHours(rnd.nextInt(8));
                    if (closedAt.isAfter(now)) closedAt = now.minusMinutes(30 + rnd.nextInt(60));
                }

                String[] titleArr = TITLES.getOrDefault(type, new String[]{"Обращение"});
                String title = titleArr[rnd.nextInt(titleArr.length)];
                String desc  = DESCRIPTIONS[rnd.nextInt(DESCRIPTIONS.length)]
                             + " Тип: " + type.getDisplayName().toLowerCase() + ".";

                Request req = requestRepo.save(Request.builder()
                    .resident(resident).master(master)
                    .title(title).description(desc).type(type).status(status)
                    .createdAt(createdAt)
                    .deadline(createdAt.plusDays(2 + rnd.nextInt(8)))
                    .closedAt(closedAt)
                    .build());
                requests.add(req);
            }

            // ── 6. Комментарии ───────────────────────────────────────────────────
            for (Request req : requests) {
                if (req.getStatus() == RequestStatus.NEW
                        || req.getStatus() == RequestStatus.CANCELLED) continue;

                LocalDateTime base = req.getCreatedAt();

                // Системный комментарий от администратора
                if (rnd.nextInt(3) > 0) {
                    commentRepo.save(RequestComment.builder()
                        .request(req).author(admin)
                        .text(ADMIN_COMMENTS[rnd.nextInt(ADMIN_COMMENTS.length)])
                        .createdAt(base.plusMinutes(5 + rnd.nextInt(120)))
                        .build());
                }

                // 1-3 комментария от мастера
                if (req.getMaster() != null) {
                    int masterMsgs = 1 + rnd.nextInt(3);
                    for (int c = 0; c < masterMsgs; c++) {
                        commentRepo.save(RequestComment.builder()
                            .request(req).author(req.getMaster())
                            .text(MASTER_COMMENTS[rnd.nextInt(MASTER_COMMENTS.length)])
                            .createdAt(base.plusHours(1 + rnd.nextInt(48)).plusMinutes(rnd.nextInt(60)))
                            .build());
                    }
                }

                // Иногда жилец отвечает
                if (rnd.nextInt(4) == 0) {
                    commentRepo.save(RequestComment.builder()
                        .request(req).author(req.getResident())
                        .text(RESIDENT_COMMENTS[rnd.nextInt(RESIDENT_COMMENTS.length)])
                        .createdAt(base.plusHours(3 + rnd.nextInt(72)))
                        .build());
                }
            }

            // ── 7. Балансовые транзакции ─────────────────────────────────────────
            // Отслеживаем баланс каждого мастера локально, чтобы не делать лишних запросов
            Map<Long, BigDecimal> balances = new HashMap<>();
            for (User m : masters) balances.put(m.getId(), BigDecimal.ZERO);

            // CREDIT — за каждую выполненную заявку с мастером и ненулевой ценой
            for (Request req : requests) {
                if (req.getStatus() != RequestStatus.DONE || req.getMaster() == null) continue;
                BigDecimal price = req.getType().getPrice();
                if (price.compareTo(BigDecimal.ZERO) == 0) continue;

                LocalDateTime txTime = req.getClosedAt() != null
                    ? req.getClosedAt().plusMinutes(5 + rnd.nextInt(30))
                    : now.minusDays(1);

                txRepo.save(BalanceTransaction.builder()
                    .master(req.getMaster()).createdBy(admin)
                    .type(TransactionType.CREDIT).amount(price)
                    .request(req).comment("Начисление за заявку №" + req.getId())
                    .createdAt(txTime).build());

                balances.merge(req.getMaster().getId(), price, BigDecimal::add);
            }

            // BONUS — 1-3 бонуса на каждого мастера
            for (User master : masters) {
                int bonuses = 1 + rnd.nextInt(3);
                for (int b = 0; b < bonuses; b++) {
                    BigDecimal amount = BigDecimal.valueOf(500 + rnd.nextInt(2000));
                    txRepo.save(BalanceTransaction.builder()
                        .master(master).createdBy(admin)
                        .type(TransactionType.BONUS).amount(amount)
                        .comment("Бонус за перевыполнение плана в " + bonusMonth(b))
                        .createdAt(now.minusDays(3 + rnd.nextInt(40)))
                        .build());
                    balances.merge(master.getId(), amount, BigDecimal::add);
                }
            }

            // FINE — у каждого второго мастера один штраф
            for (int i = 0; i < masters.size(); i++) {
                if (i % 2 != 0) continue;
                User master = masters.get(i);
                BigDecimal amount = BigDecimal.valueOf(300 + rnd.nextInt(1200));
                txRepo.save(BalanceTransaction.builder()
                    .master(master).createdBy(admin)
                    .type(TransactionType.FINE).amount(amount)
                    .comment("Нарушение сроков выполнения заявки (более 3 дней)")
                    .createdAt(now.minusDays(7 + rnd.nextInt(30)))
                    .build());
                balances.merge(master.getId(), amount.negate(), BigDecimal::add);
            }

            // PAYOUT — 3 мастерам выплатили накопленное
            List<User> shuffled = new ArrayList<>(masters);
            Collections.shuffle(shuffled, rnd);
            for (int p = 0; p < Math.min(3, shuffled.size()); p++) {
                User master = shuffled.get(p);
                BigDecimal bal = balances.getOrDefault(master.getId(), BigDecimal.ZERO);
                if (bal.compareTo(BigDecimal.valueOf(1000)) <= 0) continue;

                txRepo.save(BalanceTransaction.builder()
                    .master(master).createdBy(admin)
                    .type(TransactionType.PAYOUT).amount(bal)
                    .comment("Ежемесячная выплата заработанных средств")
                    .createdAt(now.minusDays(1 + rnd.nextInt(3)))
                    .build());
                balances.put(master.getId(), BigDecimal.ZERO);
            }

            // Записываем итоговые балансы в сущности мастеров
            for (User master : masters) {
                BigDecimal bal = balances.getOrDefault(master.getId(), BigDecimal.ZERO);
                // Баланс не может быть отрицательным
                master.setBalance(bal.max(BigDecimal.ZERO));
                userRepo.save(master);
            }

            // ── 8. Показания счётчиков ───────────────────────────────────────────
            LocalDate prevPeriod = LocalDate.now().minusMonths(1).withDayOfMonth(1);
            LocalDate currPeriod = LocalDate.now().withDayOfMonth(1);

            for (int ri = 0; ri < residents.size(); ri++) {
                User resident = residents.get(ri);
                if (resident.getBuilding() == null) continue;

                // ~25% жильцов вообще не сдают показания
                if (rnd.nextInt(100) < 25) continue;

                // Базовые значения: реалистичные начальные показания
                BigDecimal coldBase = BigDecimal.valueOf(80  + rnd.nextInt(250));
                BigDecimal hotBase  = BigDecimal.valueOf(40  + rnd.nextInt(120));
                BigDecimal gasBase  = BigDecimal.valueOf(150 + rnd.nextInt(350));
                BigDecimal elecBase = BigDecimal.valueOf(800 + rnd.nextInt(2500));

                // Месячное потребление (реалистичные диапазоны)
                BigDecimal coldDelta = BigDecimal.valueOf(3  + rnd.nextInt(6));  // 3–8 м³
                BigDecimal hotDelta  = BigDecimal.valueOf(2  + rnd.nextInt(4));  // 2–5 м³
                BigDecimal gasDelta  = BigDecimal.valueOf(10 + rnd.nextInt(22)); // 10–31 м³
                BigDecimal elecDelta = BigDecimal.valueOf(50 + rnd.nextInt(120));// 50–169 кВт·ч

                // Предыдущий месяц — время сдачи: 15–26 число прошлого месяца
                LocalDateTime prevSubmit = prevPeriod
                    .plusDays(14 + rnd.nextInt(12))
                    .atTime(9 + rnd.nextInt(11), rnd.nextInt(60));

                meterRepo.save(meterReading(resident, MeterType.COLD_WATER,  coldBase,              prevPeriod, prevSubmit));
                if (rnd.nextInt(5) > 0) // у ~80% есть горячая вода
                    meterRepo.save(meterReading(resident, MeterType.HOT_WATER,   hotBase,  prevPeriod, prevSubmit.plusMinutes(2)));
                if (rnd.nextInt(5) > 0) // у ~80% есть газ
                    meterRepo.save(meterReading(resident, MeterType.GAS,          gasBase,  prevPeriod, prevSubmit.plusMinutes(4)));
                meterRepo.save(meterReading(resident, MeterType.ELECTRICITY, elecBase,              prevPeriod, prevSubmit.plusMinutes(6)));

                // Текущий месяц — ~65% уже сдали (1–текущий день месяца)
                if (rnd.nextInt(100) < 65) {
                    int maxDay = Math.max(1, LocalDate.now().getDayOfMonth() - 1);
                    LocalDateTime currSubmit = currPeriod
                        .plusDays(rnd.nextInt(maxDay))
                        .atTime(9 + rnd.nextInt(11), rnd.nextInt(60));
                    if (currSubmit.isAfter(now)) currSubmit = now.minusHours(1);

                    meterRepo.save(meterReading(resident, MeterType.COLD_WATER,  coldBase.add(coldDelta), currPeriod, currSubmit));
                    if (rnd.nextInt(5) > 0)
                        meterRepo.save(meterReading(resident, MeterType.HOT_WATER,   hotBase.add(hotDelta),   currPeriod, currSubmit.plusMinutes(2)));
                    if (rnd.nextInt(5) > 0)
                        meterRepo.save(meterReading(resident, MeterType.GAS,          gasBase.add(gasDelta),   currPeriod, currSubmit.plusMinutes(4)));
                    meterRepo.save(meterReading(resident, MeterType.ELECTRICITY, elecBase.add(elecDelta), currPeriod, currSubmit.plusMinutes(6)));
                }
            }
            // ── 9. Новости ───────────────────────────────────────────
            List<News> newsList = new ArrayList<>();
            for (int i = 0; i < NEWS_DATA.length; i++) {
                LocalDateTime created = now.minusDays(rnd.nextInt(60));
                newsList.add(News.builder()
                        .title((String) NEWS_DATA[i][0])
                        .description((String) NEWS_DATA[i][1])
                        .category((NewsCategory) NEWS_DATA[i][2])
                        .validUntil(created.plusDays(14 + rnd.nextInt(30)))
                        .build());
            }
            newsRepo.saveAll(newsList);

            System.out.printf("[Seeder] Готово: %d домов, 1 admin, %d мастеров, %d жильцов, %d заявок, %d новостей%n",
                buildings.size(), masters.size(), residents.size(), requests.size(), newsList.size());
        };
    }

    // ══════════════════════════════════ УТИЛИТЫ ════════════════════════════════════

    /** Уникальный телефон по индексу. Формат: +7 (9XX) YYY-ZZ-WW */
    private static String genPhone(int idx) {
        return String.format("+7 (9%02d) %03d-%02d-%02d",
            (idx * 3) % 100,
            idx % 1000,
            (idx * 7) % 100,
            (idx * 11) % 100);
    }

    /** Строит взвешенный массив для случайного выбора по весам. */
    private static RequestStatus[] buildWeightedPool(RequestStatus[] values, int[] weights) {
        List<RequestStatus> pool = new ArrayList<>();
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < weights[i]; j++) pool.add(values[i]);
        }
        return pool.toArray(new RequestStatus[0]);
    }

    /** Собирает запись показания счётчика. */
    private static MeterReading meterReading(User user, MeterType type, BigDecimal value,
                                              LocalDate period, LocalDateTime submittedAt) {
        return MeterReading.builder()
            .user(user).building(user.getBuilding()).apartment(user.getApartment())
            .meterType(type).value(value).period(period).submittedAt(submittedAt)
            .build();
    }

    /** Название месяца для строки бонуса. */
    private static String bonusMonth(int offset) {
        String[] months = {"январе", "феврале", "марте", "апреле", "мае", "июне",
                           "июле", "августе", "сентябре", "октябре", "ноябре", "декабре"};
        int m = (LocalDate.now().getMonthValue() - 1 - offset + 12) % 12;
        return months[m];
    }
}
