package com.nrdhvnstd.halgventmod.network;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

/** Пакеты между клиентом и сервером. */
public final class ModNetwork {
    private static final String VERSION = "24"; // 24: создание персонажа, точки появления; 23: кузнечество (удар по жиле, мини-игры, молотобоец); 22: распил и работа на верстаке плотника; 21: шитейный стол, раскрой; 20: окно тканья; 19: книга выкроек портного; 18: гончарный круг; 17: окно рецептов кухонного стола; 16: окна шитья и выкроек кожевника; 15: замах в воздух; 14: гербовник (названия книг, содержимое, правка); 13: у точки значок до открытия, текст при наведении, описание, знамя, сведения; 12: окно реестра знамён; 11: реестр знамён; 10: забрало; 9: рассвет своим пакетом; 8: точки карты; 7: вещь с курсора каталога в инвентарь; 6: ручной подбор; 5: концепт инвентаря (снаряжение, сумки); 4: ники, очистка инвентаря, отряды

    private ModNetwork() {}

    public static void register(IEventBus modEventBus) {
        modEventBus.addListener(ModNetwork::onRegister);
    }

    private static void onRegister(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(VERSION);
        registrar.playToClient(NeedsSyncPayload.TYPE, NeedsSyncPayload.STREAM_CODEC, NeedsSyncPayload::handle);
        registrar.playToClient(CalendarSyncPayload.TYPE, CalendarSyncPayload.STREAM_CODEC, CalendarSyncPayload::handle);
        registrar.playToClient(CalendarEventsPayload.TYPE, CalendarEventsPayload.STREAM_CODEC, CalendarEventsPayload::handle);
        registrar.playToClient(LocksChunkPayload.TYPE, LocksChunkPayload.STREAM_CODEC, LocksChunkPayload::handle);
        registrar.playToClient(NameTagsSyncPayload.TYPE, NameTagsSyncPayload.STREAM_CODEC, NameTagsSyncPayload::handle);
        registrar.playToClient(SquadMembersPayload.TYPE, SquadMembersPayload.STREAM_CODEC, SquadMembersPayload::handle);
        registrar.playToClient(SquadStatePayload.TYPE, SquadStatePayload.STREAM_CODEC, SquadStatePayload::handle);
        registrar.playToClient(SquadResultPayload.TYPE, SquadResultPayload.STREAM_CODEC, SquadResultPayload::handle);
        registrar.playToServer(SquadActionPayload.TYPE, SquadActionPayload.STREAM_CODEC, SquadActionPayload::handle);
        registrar.playToServer(DrinkWaterPayload.TYPE, DrinkWaterPayload.STREAM_CODEC, DrinkWaterPayload::handle);
        registrar.playToServer(ClearInventoryPayload.TYPE, ClearInventoryPayload.STREAM_CODEC, ClearInventoryPayload::handle);
        registrar.playToServer(OpenInventoryPayload.TYPE, OpenInventoryPayload.STREAM_CODEC, OpenInventoryPayload::handle);
        registrar.playToServer(PickupPayload.TYPE, PickupPayload.STREAM_CODEC, PickupPayload::handle);
        registrar.playToServer(UseKeyPayload.TYPE, UseKeyPayload.STREAM_CODEC, UseKeyPayload::handle);
        registrar.playToClient(SoundDataPayload.TYPE, SoundDataPayload.STREAM_CODEC, SoundDataPayload::handle);
        registrar.playToServer(SoundEmitterEditPayload.TYPE, SoundEmitterEditPayload.STREAM_CODEC, SoundEmitterEditPayload::handle);
        registrar.playToServer(ToggleVisorPayload.TYPE, ToggleVisorPayload.STREAM_CODEC, ToggleVisorPayload::handle);
        registrar.playToServer(RenameKeyPayload.TYPE, RenameKeyPayload.STREAM_CODEC, RenameKeyPayload::handle);
        registrar.playToServer(MapTeleportPayload.TYPE, MapTeleportPayload.STREAM_CODEC, MapTeleportPayload::handle);
        registrar.playToClient(PointsSyncPayload.TYPE, PointsSyncPayload.STREAM_CODEC, PointsSyncPayload::handle);
        registrar.playToClient(PointsDiscoveredPayload.TYPE, PointsDiscoveredPayload.STREAM_CODEC, PointsDiscoveredPayload::handle);
        registrar.playToServer(PointEditPayload.TYPE, PointEditPayload.STREAM_CODEC, PointEditPayload::handle);
        registrar.playToClient(DawnPayload.TYPE, DawnPayload.STREAM_CODEC, DawnPayload::handle);
        registrar.playToClient(BannersSyncPayload.TYPE, BannersSyncPayload.STREAM_CODEC, BannersSyncPayload::handle);
        registrar.playToClient(BannerRegistryOpenPayload.TYPE, BannerRegistryOpenPayload.STREAM_CODEC, BannerRegistryOpenPayload::handle);
        registrar.playToServer(BannerActionPayload.TYPE, BannerActionPayload.STREAM_CODEC, BannerActionPayload::handle);
        registrar.playToClient(ArmorialTitlesPayload.TYPE, ArmorialTitlesPayload.STREAM_CODEC, ArmorialTitlesPayload::handle);
        registrar.playToClient(ArmorialContentPayload.TYPE, ArmorialContentPayload.STREAM_CODEC, ArmorialContentPayload::handle);
        registrar.playToServer(ArmorialEditPayload.TYPE, ArmorialEditPayload.STREAM_CODEC, ArmorialEditPayload::handle);
        registrar.playToServer(SwingPayload.TYPE, SwingPayload.STREAM_CODEC, SwingPayload::handle);
        registrar.playToClient(SewingOpenPayload.TYPE, SewingOpenPayload.STREAM_CODEC, SewingOpenPayload::handle);
        registrar.playToServer(SewingResultPayload.TYPE, SewingResultPayload.STREAM_CODEC, SewingResultPayload::handle);
        registrar.playToClient(CutOpenPayload.TYPE, CutOpenPayload.STREAM_CODEC, CutOpenPayload::handle);
        registrar.playToServer(CutResultPayload.TYPE, CutResultPayload.STREAM_CODEC, CutResultPayload::handle);
        registrar.playToClient(WeaveOpenPayload.TYPE, WeaveOpenPayload.STREAM_CODEC, WeaveOpenPayload::handle);
        registrar.playToServer(WeavePassPayload.TYPE, WeavePassPayload.STREAM_CODEC, WeavePassPayload::handle);
        registrar.playToClient(KitchenOpenPayload.TYPE, KitchenOpenPayload.STREAM_CODEC, KitchenOpenPayload::handle);
        registrar.playToServer(KitchenChoosePayload.TYPE, KitchenChoosePayload.STREAM_CODEC, KitchenChoosePayload::handle);
        registrar.playToClient(WheelOpenPayload.TYPE, WheelOpenPayload.STREAM_CODEC, WheelOpenPayload::handle);
        registrar.playToServer(WheelResultPayload.TYPE, WheelResultPayload.STREAM_CODEC, WheelResultPayload::handle);
        registrar.playToClient(SawOpenPayload.TYPE, SawOpenPayload.STREAM_CODEC, SawOpenPayload::handle);
        registrar.playToServer(SawCutPayload.TYPE, SawCutPayload.STREAM_CODEC, SawCutPayload::handle);
        registrar.playToClient(BenchWorkOpenPayload.TYPE, BenchWorkOpenPayload.STREAM_CODEC, BenchWorkOpenPayload::handle);
        registrar.playToServer(BenchWorkResultPayload.TYPE, BenchWorkResultPayload.STREAM_CODEC, BenchWorkResultPayload::handle);
        registrar.playToServer(OreStrikePayload.TYPE, OreStrikePayload.STREAM_CODEC, OreStrikePayload::handle);
        registrar.playToClient(SmithOpenPayload.TYPE, SmithOpenPayload.STREAM_CODEC, SmithOpenPayload::handle);
        registrar.playToServer(SmithResultPayload.TYPE, SmithResultPayload.STREAM_CODEC, SmithResultPayload::handle);
        registrar.playToServer(StrikerHitPayload.TYPE, StrikerHitPayload.STREAM_CODEC, StrikerHitPayload::handle);
        registrar.playToClient(StrikerBlowPayload.TYPE, StrikerBlowPayload.STREAM_CODEC, StrikerBlowPayload::handle);
        registrar.playToClient(CharacterOpenPayload.TYPE, CharacterOpenPayload.STREAM_CODEC, CharacterOpenPayload::handle);
        registrar.playToServer(CharacterCreatePayload.TYPE, CharacterCreatePayload.STREAM_CODEC, CharacterCreatePayload::handle);
        registrar.playToClient(CharacterResultPayload.TYPE, CharacterResultPayload.STREAM_CODEC, CharacterResultPayload::handle);
    }
}