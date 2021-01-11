//
//  PriceBalloonMarker.swift
//  BisonApp
//
//  Created by Anže Vavpetič on 18/05/2020.
//  Copyright © 2020 Facebook. All rights reserved.
//

import Charts
import Foundation
import SwiftyJSON

open class PriceBalloonMarker: BalloonMarker {

  fileprivate var avoidGraphLine: Bool = false
  fileprivate var labelns: NSMutableAttributedString?
  fileprivate var commonAttributes = [NSAttributedString.Key: Any]()
  fileprivate var boldAttributes = [NSAttributedString.Key: Any]()
  fileprivate var positiveOffsetAttributes = [NSAttributedString.Key: Any]()
  fileprivate var negativeOffsetAttributes = [NSAttributedString.Key: Any]()

  public init(
    color: UIColor, font: UIFont, textColor: UIColor, positiveColor: UIColor = UIColor.green,
    negativeColor: UIColor = UIColor.red
  ) {
    super.init(color: color, font: font, textColor: textColor)

    commonAttributes[NSAttributedString.Key.font] = font
    commonAttributes[NSAttributedString.Key.paragraphStyle] = _paragraphStyle
    
    var boldFont = font
    if let boldDescriptor = font.fontDescriptor.withSymbolicTraits(.traitBold) {
        boldFont = UIFont(descriptor: boldDescriptor, size: font.pointSize)
    }

    boldAttributes[NSAttributedString.Key.font] = boldFont
    positiveOffsetAttributes[NSAttributedString.Key.foregroundColor] = positiveColor
    negativeOffsetAttributes[NSAttributedString.Key.foregroundColor] = negativeColor
  }

  public required init?(coder aDecoder: NSCoder) {
    fatalError("init(coder:) has not been implemented")
  }

  override func drawRectOnTop(context: CGContext, point: CGPoint) -> CGRect {

    let chart = super.chartView
    let width = _size.width

    var rect = CGRect(origin: point, size: _size)

    rect.origin.y = 0

    // Show the tooltip below the chart, when the tooltip covers the chart
    let delta = CGFloat(40)
    if self.avoidGraphLine && point.y < _size.height {
      rect.origin.y = _size.height + delta
    }

    rect.origin.x -= width / 2.0
    rect.origin.x = max(0, min(rect.origin.x, (chart?.bounds.width ?? width) - width))

    drawFillRect(context: context, rect: rect)

    rect.origin.y += self.insets.top
    rect.size.height -= self.insets.top + self.insets.bottom

    return rect
  }

  open override func refreshContent(entry: ChartDataEntry, highlight: Highlight) {
    var label: String

    if let candleEntry = entry as? CandleChartDataEntry {

      label = candleEntry.close.description
    } else {
      label = entry.y.description
    }

    if let object = entry.data as? JSON {
      if object["marker"].exists() {
        let marker = object["marker"].dictionaryValue

        // Check for additional marker data
        if let entity = marker["entity"]?.stringValue,
          let price = marker["price"]?.stringValue,
          let priceDiff = marker["priceDiff"]?.stringValue,
          let dateTime = marker["dateTime"]?.stringValue,
          let direction = marker["direction"]?.stringValue
        {
          label = "\(entity) \(price)\n\(priceDiff)\n\(dateTime)"

          let priceRange = NSRange(location: entity.count + 1, length: price.count)
          let priceDiffRange = NSRange(location: priceRange.upperBound + 1, length: priceDiff.count)

          labelns = NSMutableAttributedString(string: label, attributes: commonAttributes)
          labelns?.addAttributes(boldAttributes, range: priceRange)

          switch direction {
          case "positive":
            labelns?.addAttributes(positiveOffsetAttributes, range: priceDiffRange)
          case "negative":
            labelns?.addAttributes(negativeOffsetAttributes, range: priceDiffRange)
          default: break
          }

        } else {
          // otherwise
          label = object["marker"].stringValue
          labelns = NSMutableAttributedString(string: label, attributes: commonAttributes)
        }

        if highlight.stackIndex != -1 && object["marker"].array != nil {
          label = object["marker"].arrayValue[highlight.stackIndex].stringValue
          labelns = NSMutableAttributedString(string: label, attributes: commonAttributes)
        }
      }
    }

    _labelSize = labelns?.size() ?? CGSize.zero
    _size.width = _labelSize.width + self.insets.left + self.insets.right
    _size.height = _labelSize.height + self.insets.top + self.insets.bottom
    _size.width = max(minimumSize.width, _size.width)
    _size.height = max(minimumSize.height, _size.height)
  }

  func setAvoidGraphLine(_ avoidGraphLine: Bool) {
    self.avoidGraphLine = avoidGraphLine
  }

  open override func draw(context: CGContext, point: CGPoint) {
    if labelns == nil || labelns?.length == 0 {
      return
    }

    context.saveGState()

    let rect = drawRectOnTop(context: context, point: point)

    UIGraphicsPushContext(context)

    labelns?.draw(in: rect)

    UIGraphicsPopContext()

    context.restoreGState()
  }

}
