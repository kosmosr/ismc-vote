![Maven Package && Run App](https://github.com/kosmosr/ismc-vote/workflows/Maven%20Package%20&&%20Run%20App/badge.svg?event=schedule)

## 某网站的自动投票程序, 加入了github actions自动运行
## 需要设置的环境变量
- pd_id: ${{ secrets.pd_id }}
- pd_key: ${{ secrets.pd_key }}
- proxy_auth_user: ${{ secrets.proxy_auth_user }}
- proxy_auth_password: ${{ secrets.proxy_auth_password }}
- orderId: ${{ secrets.orderId }}
- signature: ${{ secrets.signature }}
- sc_key: ${{ secrets.sc_key }}
