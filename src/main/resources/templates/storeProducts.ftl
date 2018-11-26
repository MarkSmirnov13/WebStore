<#import "parts/common.ftl" as C>
<@C.page>


<div class="container mt-5">
    <h2 class="mb-3">${storeName}'s product list</h2>
        <div class="row">
                <#list productTypeStores as productTypeStore>
                    <div class="col-md-3 mt-4">
                        <div class="card text-center img">
                            <#if productTypeStore.product.filename??>
                                <div class="wrapper">
                                    <img class="card-img-top" src="/img/${productTypeStore.product.filename}" alt="Card image cap">
                                </div>
                            </#if>
                            <div class="card-body">
                                <h5 class="card-title">${productTypeStore.product.name}</h5>
                                <p>${productTypeStore.product.price} ₽</p>
                                <hr class="hr">
                                <p class="see-more">
                                    <a class="seemore" style="text-decoration: none; color: #167ffb" data-toggle="collapse" href="#collapse${productTypeStore.product.id}" aria-expanded="false" aria-controls="collapseExample">
                                        See more
                                    </a>
                                </p>
                                <div class="collapse" id="collapse${productTypeStore.product.id}">
                                    <div class="card-body">
                                        Lorem Ipsum is simply dummy text of the printing and typesetting industry.
                                    </div>
                                </div>
                            </div>
                            <div class="card-footer text-muted" style="background-color: white">
                                <div class="row">
                                    <div class="col">
                                        <a href="#" class="seemore" style="text-decoration: none; float: right;">Add to cart</a>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </#list>
        </div>
</div>

</@C.page>