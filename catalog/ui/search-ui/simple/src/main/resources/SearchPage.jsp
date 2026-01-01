<%@ page import="org.codice.ddf.ui.searchui.simple.properties.UiConfigurationPropertiesFactory,org.codice.ddf.ui.searchui.simple.properties.UiConfigurationProperties" %>
<!DOCTYPE html>
<!--
/**
 * Copyright (c) Codice Foundation
 *
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details. A copy of the GNU Lesser General Public License is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 *
 **/
 -->

<html>
<head>
<meta charset="utf-8">

<%
  UiConfigurationProperties props = UiConfigurationPropertiesFactory.getInstance().getProperties();
%>
<title><%=props.getProductName()%> Search</title>
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<meta name="description" content="">
<meta name="author" content="">

<link href="lib/bootstrap-5.3.3/css/bootstrap.min.css" rel="stylesheet">
<link href="lib/fontawesome-6.5.1/css/all.min.css" rel="stylesheet">

<link href="lib/jquery/css/smoothness/jquery-ui-1.13.3.custom.min.css" rel="stylesheet">
<link href="lib/jquery/css/plugin/jquery-ui-timepicker-addon.css" rel="stylesheet">

<!-- These CSS files have been compressed and aggregated into Search-min.css.  The list is here for easy modification for
     the sake of debugging.
     TODO: Leverage something like http://www.html5rocks.com/en/tutorials/developertools/sourcemaps/  for a better solution
 -->
<!--
<link href="css/searchPage.css?bust=${timestamp}" rel="stylesheet">
<link href="css/recordView.css?bust=${timestamp}" rel="stylesheet">
-->
<link href="css/Search-min.css?bust=${timestamp}" rel="stylesheet">

<style media=screen type="text/css">
	.banner {
		color: <%=props.getColor()%>;
		background: <%=props.getBackground()%>;
	}
	/* Bootstrap 5 compatibility styles */
	.search-controls {
		position: sticky;
		top: 60px;
	}
	.add-on-label {
		min-width: 80px;
	}
</style>

</head>
<body>

	<nav class="navbar navbar-expand-lg navbar-dark bg-dark fixed-top">

		 <% String h = props.getHeader();
		    if(h.trim().length() > 0) { %>
             <div class="banner w-100 text-center"><%=h %></div>
		 <% } %>

		<div class="container">
			<a class="navbar-brand" href="#">
			    <i class="fa-solid fa-globe text-white"></i> <%=props.getProductName()%>
            </a>
			<button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNav">
				<span class="navbar-toggler-icon"></span>
			</button>
			<div class="collapse navbar-collapse" id="navbarNav">
				<ul class="navbar-nav">
					<li class="nav-item"><a class="nav-link active" href="#">Search</a></li>
				</ul>
				<ul class="navbar-nav ms-auto">
					<li class="nav-item">
					    <a class="nav-link" href="SearchHelp.html?title=<%=props.getProductName()%>">
					        Help
                        </a>
                    </li>
				</ul>
			</div>
		</div>
	</nav>

	<!-- Metacard Modal -->
	<div id="metacardModal" class="modal fade" tabindex="-1" role="dialog"
		aria-labelledby="myModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title">title</h5>
					<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				</div>
				<div class="modal-body"></div>
			</div>
		</div>
	</div>

	<div class="main container-fluid clear-top">
		<div class="row">


			<div class="col-md-3">
				<form id="searchForm" class="search-controls"
					action="../../services/catalog/query" method="get">
					<ul class="list-group rounded p-3 bg-light">
						<input type="hidden" name="format" value="geojson"></input>
						<input type="hidden" name="start" value="1"></input>


						<li class="list-group-item bg-light border-0 fw-bold">Keywords</li>
						<li class="list-group-item bg-light border-0">
							<div class="input-group">
								<input name="q" type="text" class="form-control">
							</div>
						</li>

						<li class="list-group-item bg-light border-0"><hr class="my-2"></li>

						<li class="list-group-item bg-light border-0 fw-bold">Time</li>
						<li class="list-group-item bg-light border-0">
							<div class="btn-group" role="group">
								<input type="radio" class="btn-check" name="timeRadio" id="noTemporalBtn" autocomplete="off" checked>
								<label class="btn btn-outline-secondary btn-sm" for="noTemporalBtn" data-bs-toggle="tab" data-bs-target="#notemporal">Any</label>

								<input type="radio" class="btn-check" name="timeRadio" id="relativeTimeBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="relativeTimeBtn" data-bs-toggle="tab" data-bs-target="#time_relative">Relative</label>

								<input type="radio" class="btn-check" name="timeRadio" id="absoluteTimeBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="absoluteTimeBtn" data-bs-toggle="tab" data-bs-target="#time_absolute">Absolute</label>
							</div>
							<div class="tab-content mt-2">
								<div id="notemporal" name="notemporal" class="tab-pane show active"></div>
								<div id="time_absolute" name="time_absolute" class="tab-pane">
									<input type="hidden" name="dtstart" value=""> <input
										type="hidden" name="dtend" value="">

									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">Begin <i
											class="fa-solid fa-clock ms-1"></i></span>
										<input id="absoluteStartTime"
											name="absoluteStartTime" type="text" class="form-control" />
									</div>
									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">End <i
											class="fa-solid fa-clock ms-1"></i></span>
										<input id="absoluteEndTime"
											name="absoluteEndTime" type="text" class="form-control" />
									</div>
									<div class="alert alert-warning" id="timeAbsoluteWarning">
										Warning! If either value is unpopulated, the search will not
										use any temporal filters</div>

								</div>
								<div id="time_relative" name="time_relative" class="tab-pane">
									<div class="row">
										<input type="hidden" name="dtoffset" value="">
										<div class="col-12 input-group mb-2">
											<span class="input-group-text add-on-label">Last</span>
											<input id="offsetTime" class="form-control" name="offsetTime"
												type="number" onchange="updateOffset()" />
											<select id="offsetTimeUnits" class="form-select"
												name="offsetTimeUnits" onchange="updateOffset()">
												<option value="seconds">seconds</option>
												<option value="minutes" selected="selected">minutes</option>
												<option value="hours">hours</option>
												<option value="days">days</option>
												<option value="weeks">weeks</option>
												<option value="months">months</option>
												<option value="years">years</option>
											</select>
										</div>
										<div class="alert alert-warning" id="timeRelativeWarning">
											Warning! If the 'Last' duration is unpopulated, the search
											will not use any temporal filters</div>
									</div>
								</div>
							</div>
						</li>

						<li class="list-group-item bg-light border-0"><hr class="my-2"></li>

						<li class="list-group-item bg-light border-0 fw-bold">Location</li>
						<li class="list-group-item bg-light border-0">
							<div class="btn-group" role="group">
								<input type="radio" class="btn-check" name="locationRadio" id="noLocationBtn" autocomplete="off" checked>
								<label class="btn btn-outline-secondary btn-sm" for="noLocationBtn" data-bs-toggle="tab" data-bs-target="#nogeo">Any</label>

								<input type="radio" class="btn-check" name="locationRadio" id="pointRadiusBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="pointRadiusBtn" data-bs-toggle="tab" data-bs-target="#pointradius">Point-Radius</label>

								<input type="radio" class="btn-check" name="locationRadio" id="bboxBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="bboxBtn" data-bs-toggle="tab" data-bs-target="#boundingbox">Bounding Box</label>
							</div>
							<div class="tab-content mt-2">
								<div id="nogeo" class="tab-pane show active"></div>
								<div id="pointradius" class="tab-pane">
									<input type="hidden" name="radius" value=""> <input
										type="hidden" name="lat" value=""> <input
										type="hidden" name="lon" value="">

									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">Latitude<i
											class="fa-solid fa-globe ms-1"></i></span>
										<input class="form-control" id="latitude"
											name="latitude" type="number" min="-90" max="90" step="any"
											onchange="updatePointRadius()" placeholder="" />
										<span class="input-group-text">&deg;</span>
									</div>
									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">Longitude<i
											class="fa-solid fa-globe ms-1"></i></span>
										<input class="form-control" id="longitude"
											name="longitude" type="number" min="-180" max="180"
											step="any" onchange="updatePointRadius()" placeholder="" />
										<span class="input-group-text">&deg;</span>
									</div>

									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">Radius<i
											class="fa-solid fa-plus ms-1"></i></span>
										<input class="form-control" id="radiusValue"
											name="radiusValue" type="number" min="0" step="any" placeholder=""
											onchange="updatePointRadius()" />
										<select id="radiusUnits" class="form-select"
											name="radiusUnits" onchange="updatePointRadius()">
											<option value="meters" selected="selected">meters</option>
											<option value="kilometers">kilometers</option>
											<option value="feet">feet</option>
											<option value="yards">yards</option>
											<option value="miles">miles</option>
										</select>
									</div>
									<div class="alert alert-warning" id="pointRadiusWarning">
										Warning! If any Point-Radius value is unpopulated, the search
										will not use any location filters</div>
								</div>

								<div id="boundingbox" class="tab-pane">
									<input type="hidden" name="bbox" value="">
									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">West <i
											class="fa-solid fa-globe ms-1"></i></span>
										<input class="form-control" id="west"
											name="west" type="number" min="-180" max="180" step="any"
											onchange="updateBoundingBox()" placeholder="" />
										<span class="input-group-text">&deg;</span>
									</div>
									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">South <i
											class="fa-solid fa-globe ms-1"></i></span>
										<input class="form-control" id="south"
											name="south" type="number" min="-90" max="90" step="any"
											onchange="updateBoundingBox()" placeholder="" />
										<span class="input-group-text">&deg;</span>
									</div>
									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">East <i
											class="fa-solid fa-globe ms-1"></i></span>
										<input class="form-control" id="east"
											name="east" type="number" min="-180" max="180" step="any"
											onchange="updateBoundingBox()" placeholder="" />
										<span class="input-group-text">&deg;</span>
									</div>
									<div class="input-group mb-2">
										<span class="input-group-text add-on-label">North <i
											class="fa-solid fa-globe ms-1"></i></span>
										<input class="form-control" id="north"
											name="north" type="number" min="-90" max="90" step="any"
											onchange="updateBoundingBox()" placeholder="" />
										<span class="input-group-text">&deg;</span>
									</div>
									<div class="alert alert-warning" id="boundingBoxWarning">
										Warning! If any field is unpopulated, the search will not use
										any location filters</div>
								</div>

								<div id="wkt" class="tab-pane">
									<textarea rows="3" class="form-control"></textarea>
								</div>
							</div>
						</li>

						<li class="list-group-item bg-light border-0"><hr class="my-2"></li>

						<li class="list-group-item bg-light border-0 fw-bold">Type</li>
						<li class="list-group-item bg-light border-0">
							<div class="btn-group" role="group">
								<input type="radio" class="btn-check" name="typeRadio" id="noTypeBtn" autocomplete="off" checked>
								<label class="btn btn-outline-secondary btn-sm" for="noTypeBtn" data-bs-toggle="tab" data-bs-target="#noTypeTab">Any</label>

								<input type="radio" class="btn-check" name="typeRadio" id="typeBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="typeBtn" data-bs-toggle="tab" data-bs-target="#typeTab">Specific Types</label>
							</div>
							<div class="tab-content mt-2">
								<div id="noTypeTab" class="tab-pane show active"></div>
								<div id="typeTab" class="tab-pane">
									<input type="hidden" name="type" value="">
									<select id="typeList" name="typeList" class="form-select"
										onchange="updateType()">
									</select>
								</div>
							</div>
						</li>

						<li class="list-group-item bg-light border-0"><hr class="my-2"></li>

						<li class="list-group-item bg-light border-0 fw-bold">Additional Sources</li>
						<li class="list-group-item bg-light border-0"><input type="hidden" name="src" value="">
							<div class="btn-group" role="group">
								<input type="radio" class="btn-check" name="fedRadio" id="noFedBtn" autocomplete="off" checked>
								<label class="btn btn-outline-secondary btn-sm" for="noFedBtn" data-bs-toggle="tab" data-bs-target="#nofed">None</label>

								<input type="radio" class="btn-check" name="fedRadio" id="enterpriseFedBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="enterpriseFedBtn" data-bs-toggle="tab" data-bs-target="#nofed">All Sources</label>

								<input type="radio" class="btn-check" name="fedRadio" id="selectedFedBtn" autocomplete="off">
								<label class="btn btn-outline-secondary btn-sm" for="selectedFedBtn" data-bs-toggle="tab" data-bs-target="#sources">Specific Sources</label>
							</div>
							<div class="tab-content mt-2">
								<div id="nofed" class="tab-pane show active"></div>
								<div id="sources" class="tab-pane">
									<div id="scrollableSources" class="scrollable">
										<select id="federationSources" class="form-select" multiple="multiple"
											onchange="updateFederation()">
										</select>
									</div>
									<div class="alert alert-warning" id="federationListWarning">
										Warning! If no selections are made, the search will use 'All
										Sources'</div>
								</div>
							</div></li>

						<li class="list-group-item bg-light border-0"><hr class="my-2"></li>

						<li class="list-group-item bg-light border-0 fw-bold">Page Size</li>
						<li class="list-group-item bg-light border-0">
							<div class="input-group">
								<span class="input-group-text">Results per Page</span>
								<select id="count" class="form-select" type="number" name="count">
									<option value="5">5</option>
									<option value="10" selected="selected">10</option>
									<option value="25">25</option>
									<option value="100">100</option>
									<option value="500">500</option>
									<option value="1000">1000</option>
								</select>
							</div>
						</li>

						<li class="list-group-item bg-light border-0">
							<div class="d-flex gap-2">
								<button class="btn btn-primary" type="submit">
									<i class="fa-solid fa-magnifying-glass"></i> Search
								</button>
								<button class="btn btn-secondary" type="reset" onClick="resetForm()">Clear</button>
							</div>
						</li>
					</ul>
				</form>
			</div>
			<div id="contentView" class="col-md-9">
				<div id="recordView" class="record-view" style="display: none;">
					<div id="recordViewHeader" class='results-header row' style="position: sticky; top: 60px; background: white; z-index: 100;">
						<div class='col-6'>
							<nav aria-label="Record navigation">
								<ul class="pagination">
									<li id="backToResultsBtn" class="page-item"><a class="page-link">Back to Results</a></li>
								</ul>
							</nav>
						</div>
						<div class='col-6 text-end'>
							<nav aria-label="Record navigation">
								<ul class="pagination justify-content-end">
									<li id="previousRecordLi" class="page-item"><a class="page-link">Previous</a></li>
									<li id="nextRecordLi" class="page-item"><a class="page-link">Next</a></li>
								</ul>
							</nav>
						</div>
					</div>

					<!--  this div contains the 'record view.'  Eventually we want to
							make this re-usable somehow. -->
					<div id="recordContentDiv">
					</div>
					<!-- End 'record view' area -->

				</div>
				<div id="loadingView" class="msgView">
					<div class="msgViewContainer">
						<div class="msgViewContents">
							<p>
								<img src="images/ajax-loader.gif" /> Loading ...
							</p>
						</div>
					</div>
				</div>
				<div id="errorView" class="msgView">
					<div class="msgViewContainer">
						<div class="msgViewContents">
							<p>
							<span class="fa-solid fa-exclamation fa-2x"></span> <strong>Error!</strong>
							<button type="btn" class="btn btn-sm btn-outline-secondary" onClick="hideError()">&times; Close</button>
							<div id="errorText"></div>
						</div>
					</div>
				</div>
					<div id="resultsView">
						<div class="results-header row" style="position: sticky; top: 60px; background: white; z-index: 100;">

							<div class="resultsCount col-6">
								<p id="countTotal" class="lead">Total Results: 0</p>
							</div>
							<div class="col-6 text-end">
								<nav aria-label="Page navigation">
									<ul id="pages" class="pagination justify-content-end">
									</ul>
								</nav>
							</div>
						</div>
					<div class="row results">
						<table class="table table-striped">
							<thead>
								<tr>
									<th>Title</th>
									<th>Source</th>
									<th>Location</th>
									<th>Time</th>
								</tr>
							</thead>
							<tbody id="resultsList">
							</tbody>
						</table>
					</div>
					<div class="row">
						<p class="text-end">
							<a href="#">Back to top</a>
						</p>
						<br>
					</div>
				</div>
			</div>
		</div>
	</div>

	<% String f = props.getFooter();
		if(f.trim().length() > 0) { %>
		   <div class="fixed-bottom banner text-center"><%=f %></div>
        <% } %>


	<!-- Placed at the end of the document so the pages load faster -->

	<script type="text/javascript" src="lib/jquery/js/jquery-3.7.1.min.js"></script>
	<script type="text/javascript" src="lib/jquery/js/jquery-ui-1.13.3.min.js"></script>

    <script type="text/javascript" src="lib/underscore-1.13.7/underscore-min.js"></script>


	<script type="text/javascript" src="lib/bootstrap-5.3.3/js/bootstrap.bundle.min.js"></script>
	<script type="text/javascript" src="lib/jquery/js/plugin/purl.js"></script>
	<script type="text/javascript" src="lib/jquery/js/plugin/jquery-ui-datepicker-4digitYearOverride-addon.js"></script>
	<script type="text/javascript" src="lib/jquery/js/plugin/jquery-ui-timepicker-addon.js"></script>
	<script type="text/javascript" src="lib/handlebars-4.7.8/handlebars.js"></script>

<!-- These scripts have been compressed and aggregated into Search-min.js.  The list is here for easy modification for
     the sake of debugging.
     TODO: Leverage something like http://www.html5rocks.com/en/tutorials/developertools/sourcemaps/  for a better solution
 -->
<!--
	<script type="text/javascript" src="js/searchMessagingDirect.js?bust=${timestamp}"></script>
	<script type="text/javascript" src="js/recordView.js?bust=${timestamp}"></script>
	<script type="text/javascript" src="js/metadataHelper.js?bust=${timestamp}"></script>
	<script type="text/javascript" src="js/viewSwitcher.js?bust=${timestamp}"></script>
	<script type="text/javascript" src="js/searchPage.js?bust=${timestamp}"></script>
 -->

 	<script type="text/javascript" src="js/Search-min.js?bust=${timestamp}"></script>

    <!-- Usage Modal -->
    <!-- Only show if the modal is enabled -->
    <% if(props.getSystemUsageEnabled() == true) { %>
        <!-- Do not allow users to click outside of the modal to dismiss it, the OK button must be pressed -->
        <script>
            jQuery(function () {
                const usageModal = new bootstrap.Modal(document.getElementById('usageModal'), {
                    backdrop: 'static',
                    keyboard: false
                });
                usageModal.show();
            })
        </script>
        <div id="usageModal" class="modal fade" tabindex="-1" role="dialog"
            aria-labelledby="myModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h5 class="modal-title"><%=props.getSystemUsageTitle()%></h5>
                    </div>
                    <div class="modal-body"><%=props.getSystemUsageMessage()%></div>
                    <div class="modal-footer">
                        <button class="btn btn-primary" data-bs-dismiss="modal" aria-hidden="true">OK</button>
                    </div>
                </div>
            </div>
        </div>
    <% } %>

</body>
</html>
