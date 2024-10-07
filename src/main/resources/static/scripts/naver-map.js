var HOME_PATH = window.HOME_PATH || '.';

var cityhall = new naver.maps.LatLng(36.54665885700319, 128.73769979765095),
    map = new naver.maps.Map('map', {
        center: cityhall.destinationPoint(0, 500),
        zoom: 15
    }),
    marker = new naver.maps.Marker({
        map: map,
        position: cityhall
    });

var contentString = [
        '<div class="iw_inner">',
        '   <h3>안동 프라하 레스토랑</h3>',
        '   <p>경북 안동시 아늑길 10<br />',
        '       <a href="http://naver.me/GyeNvE1h" target="_blank">네이버 맵에서 확인하기</a>',
        '   </p>',
        '</div>'
    ].join('');

var infowindow = new naver.maps.InfoWindow({
    content: contentString
});

naver.maps.Event.addListener(marker, "click", function(e) {
    if (infowindow.getMap()) {
        infowindow.close();
    } else {
        infowindow.open(map, marker);
    }
});

infowindow.open(map, marker);