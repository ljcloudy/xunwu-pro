function load(city, regions, aggData) {
    // 百度地图API功能
    var map = new BMap.Map("allmap", {minZoom: 12}); // 创建实例。设置地图显示最大级别为城市
    var point = new BMap.Point(116.404185,39.915574); // 城市中心
    map.centerAndZoom(point, 12); // 初始化地图，设置中心点坐标及地图级别

    map.addControl(new BMap.NavigationControl({enableGeolocation: true})); // 添加比例尺控件
    map.addControl(new BMap.ScaleControl({anchor: BMAP_ANCHOR_TOP_LEFT})); // 左上角
    map.enableScrollWheelZoom(true); // 开启鼠标滚轮缩放
}